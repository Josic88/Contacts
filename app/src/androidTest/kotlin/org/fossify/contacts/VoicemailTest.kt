package org.fossify.contacts

import android.Manifest
import android.content.ContentProviderOperation
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.provider.ContactsContract
import android.provider.ContactsContract.AggregationExceptions
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.fossify.commons.helpers.CONTACT_ID
import org.fossify.commons.helpers.IS_PRIVATE
import org.fossify.commons.helpers.LocalContactsHelper
import org.fossify.commons.helpers.SMT_PRIVATE
import org.fossify.commons.models.contacts.Contact
import org.fossify.contacts.activities.ViewContactActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class VoicemailTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val resolver = context.contentResolver
    private val rawRows = mutableListOf<Uri>()
    private val privateRows = mutableListOf<Long>()

    @Before fun grantContacts() {
        for (permission in listOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS)) {
            instrumentation.uiAutomation.grantRuntimePermission(context.packageName, permission)
        }
    }

    @After fun removeFixtures() {
        rawRows.forEach { resolver.delete(it, null, null) }
        if (privateRows.isNotEmpty()) LocalContactsHelper(context).deleteContactIds(privateRows)
    }

    private fun addRaw(): Uri {
        val ops = arrayListOf(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null).withValue(RawContacts.ACCOUNT_NAME, null).build(),
            ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.GIVEN_NAME, "Test-" + UUID.randomUUID()).build()
        )
        return requireNotNull(resolver.applyBatch(ContactsContract.AUTHORITY, ops)[0].uri).also { rawRows.add(it) }
    }

    private fun column(uri: Uri, name: String): Long =
        requireNotNull(resolver.query(uri, arrayOf(name), null, null, null)).use {
            check(it.moveToFirst())
            it.getLong(0)
        }

    private fun open(rawId: Int, privateContact: Boolean = false): ActivityScenario<ViewContactActivity> =
        ActivityScenario.launch(Intent(context, ViewContactActivity::class.java).apply {
            putExtra(CONTACT_ID, rawId)
            putExtra(IS_PRIVATE, privateContact)
        })

    private fun waitForMenu(scenario: ActivityScenario<ViewContactActivity>, matches: (MenuItem) -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + 15_000
        do {
            var ready = false
            scenario.onActivity { activity ->
                val item = activity.findViewById<Toolbar>(R.id.contact_toolbar).menu.findItem(R.id.calls_to_voicemail)
                ready = item.isVisible && matches(item)
            }
            if (ready) return
            SystemClock.sleep(100)
        } while (SystemClock.elapsedRealtime() < deadline)
        fail("Timed out waiting for the contact's voicemail menu state")
    }

    private fun toggle(scenario: ActivityScenario<ViewContactActivity>) {
        scenario.onActivity {
            assertTrue(it.findViewById<Toolbar>(R.id.contact_toolbar).menu.performIdentifierAction(R.id.calls_to_voicemail, 0))
        }
    }

    @Test fun togglesAggregateFromRawContactAndPersists() {
        val first = addRaw()
        val second = addRaw()
        val control = addRaw()
        resolver.update(AggregationExceptions.CONTENT_URI, ContentValues().apply {
            put(AggregationExceptions.TYPE, AggregationExceptions.TYPE_KEEP_TOGETHER)
            put(AggregationExceptions.RAW_CONTACT_ID1, ContentUris.parseId(first))
            put(AggregationExceptions.RAW_CONTACT_ID2, ContentUris.parseId(second))
        }, null, null)
        val aggregate = column(first, RawContacts.CONTACT_ID)
        assertEquals(aggregate, column(second, RawContacts.CONTACT_ID))
        val rawId = listOf(first, second).map(ContentUris::parseId).first { it != aggregate }.toInt()
        val target = ContentUris.withAppendedId(Contacts.CONTENT_URI, aggregate)
        val other = ContentUris.withAppendedId(Contacts.CONTENT_URI, column(control, RawContacts.CONTACT_ID))

        open(rawId).use { view ->
            waitForMenu(view) { it.isEnabled && it.isCheckable && !it.isChecked }
            toggle(view)
            waitForMenu(view) { it.isEnabled && it.isChecked }
            assertEquals(1L, column(target, Contacts.SEND_TO_VOICEMAIL))
            assertEquals(0L, column(other, Contacts.SEND_TO_VOICEMAIL))
        }
        open(rawId).use { view ->
            waitForMenu(view) { it.isEnabled && it.isChecked }
            toggle(view)
            waitForMenu(view) { it.isEnabled && it.isCheckable && !it.isChecked }
            assertEquals(0L, column(target, Contacts.SEND_TO_VOICEMAIL))
        }
    }

    @Test fun privateContactShowsUnavailable() {
        val name = "Private-" + UUID.randomUUID()
        val helper = LocalContactsHelper(context)
        assertTrue(helper.insertOrUpdateContact(Contact(id = 0, contactId = 0, firstName = name, source = SMT_PRIVATE)))
        val id = helper.getAllContacts().single { it.firstName == name }.id
        privateRows.add(id.toLong())
        open(id, privateContact = true).use { view ->
            waitForMenu(view) {
                !it.isEnabled && !it.isCheckable && it.title == context.getString(R.string.voicemail_private)
            }
        }
    }
}

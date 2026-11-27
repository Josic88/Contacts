package org.fossify.contacts.helpers

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.ContactsContract.Contacts

internal data class VoicemailOption(val lookupUri: Uri, val enabled: Boolean) {
    companion object {
        fun read(resolver: ContentResolver, contactId: Long): VoicemailOption? {
            val uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId)
            val columns = arrayOf(Contacts.LOOKUP_KEY, Contacts.SEND_TO_VOICEMAIL)
            return resolver.query(uri, columns, null, null, null)?.use { cursor ->
                if (!cursor.moveToFirst()) return@use null
                val key = cursor.getString(0) ?: return@use null
                VoicemailOption(Contacts.getLookupUri(contactId, key), cursor.getInt(1) != 0)
            }
        }
    }

    fun setEnabled(resolver: ContentResolver, value: Boolean): Boolean {
        val values = ContentValues(1).apply { put(Contacts.SEND_TO_VOICEMAIL, if (value) 1 else 0) }
        if (resolver.update(lookupUri, values, null, null) != 1) return false
        return resolver.query(lookupUri, arrayOf(Contacts.SEND_TO_VOICEMAIL), null, null, null)?.use {
            it.moveToFirst() && (it.getInt(0) != 0) == value
        } == true
    }
}

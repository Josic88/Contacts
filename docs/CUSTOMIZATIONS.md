# Personnalisations de Contacts

## Installation côte à côte

| Élément | Choix du fork |
| --- | --- |
| Package installé | `org.fossify.contacts.personal` (release), suffixe `.debug` en développement |
| Namespace Kotlin et ressources | `org.fossify.contacts` pour garder les imports et les classes de l'amont |
| Lanceur | Libellé personnel en anglais et en français. Les alias de couleur utilisent le nom attendu par Commons et ciblent la classe SplashActivity d'origine. |
| FileProvider et initialisation AndroidX | Les autorités dérivées de ${applicationId} suivent le package installé. |
| Fournisseur privé Fossify | L'autorité fixe `org.fossify.commons.contactsprovider` appartient à Contacts officiel. Le fork omet ce fournisseur exporté et masque le réglage de partage associé. |
| Données | Les contacts Android sont communs aux applications autorisées du même profil. Chaque installation possède sa propre base privée et ses propres préférences. |
| Fossify Phone officiel | Il interroge les contacts système et connaît le fournisseur privé officiel. Les fiches privées du fork restent internes. Les ouvertures explicitement dirigées vers le package Contacts officiel peuvent continuer à y aboutir. |

Le fork ne migre aucune fiche et ne copie pas les contacts privés dans Android. Les permissions de l'application officielle ne se transmettent pas au nouveau package. Le renvoi utilise les permissions Contacts déjà déclarées par Fossify. Commons et Phone proviennent de l'amont.

## Renvoi des appels vers la messagerie

Dans le menu de la fiche, « Renvoyer les appels vers la messagerie » porte une coche quand le fournisseur Android indique que le renvoi est actif. Un appui demande l'état inverse. L'application relit l'état après écriture et au retour sur la fiche. Une erreur ou un refus d'écriture est indiqué à l'écran.

| Fiche ouverte | Identité et comportement |
| --- | --- |
| Contact système | Fossify charge la fiche à partir d'un ID brut ou d'une clé de recherche. `Contact.contactId` correspond à `Data.CONTACT_ID`. La lecture se fait sur l'agrégat, l'écriture sur son URI de recherche, pour le seul champ `Contacts.SEND_TO_VOICEMAIL`. |
| Plusieurs fiches brutes du même agrégat | Le réglage s'applique au contact Android entier et à ses numéros reconnus par Telecom. |
| Homonymes regroupés à l'écran | Si les éléments visibles ont plusieurs identités Android ou incluent une fiche privée, le réglage est indisponible. Désactiver le regroupement des doublons dans les paramètres Fossify pour ouvrir chaque fiche séparément. |
| Fiche privée Fossify | Le menu explique l'indisponibilité. Sa ligne en base locale ne désigne aucun agrégat système. Aucun rapprochement par nom ou numéro. |

Android documente ce champ entier depuis l'API 5. QuickContactActivity d'AOSP conditionne la visibilité de son propre menu à une version antérieure à Android 6. Dans la branche 17 de GrapheneOS, CallerInfo lit `PhoneLookup.SEND_TO_VOICEMAIL` et CallsManager installe DirectToVoicemailFilter. Pour un numéro identifié dont le drapeau est actif, le filtre demande le rejet avant présentation de l'appel. Le dépôt d'un message dépend ensuite de la messagerie et du renvoi sur rejet chez l'opérateur. Le mécanisme n'édite aucune liste de numéros bloqués, aucun SMS et n'installe aucun service d'écran d'appels. Un numéro masqué ou non reconnu ne permet pas d'identifier le contact.

[Contrat Android](https://developer.android.com/reference/android/provider/ContactsContract.ContactOptionsColumns#SEND_TO_VOICEMAIL) · [Menu AOSP](https://android.googlesource.com/platform/packages/apps/Contacts/+/922019361da99534505d18b8c669a04d1cd0bc00/src/com/android/contacts/quickcontact/QuickContactActivity.java) · [Lecture du champ dans GrapheneOS](https://github.com/GrapheneOS/platform_packages_services_Telecomm/blob/21585d350141ccae06e0a85703290f1e06730831/src/com/android/server/telecom/util/CallerInfo.java) · [Filtre GrapheneOS](https://github.com/GrapheneOS/platform_packages_services_Telecomm/blob/21585d350141ccae06e0a85703290f1e06730831/src/com/android/server/telecom/callfiltering/DirectToVoicemailFilter.java) · [Montage du filtre](https://github.com/GrapheneOS/platform_packages_services_Telecomm/blob/21585d350141ccae06e0a85703290f1e06730831/src/com/android/server/telecom/CallsManager.java) · [URI privée de Commons](https://github.com/FossifyOrg/Commons/blob/7.1.3/commons/src/main/kotlin/org/fossify/commons/helpers/MyContactsContentProvider.kt)

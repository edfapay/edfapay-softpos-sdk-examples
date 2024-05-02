package tools

import kotlin.Exception

object Errors {
    val missingPartnerCode = Exception(
"""
Missing permanent User/System environment variable named: 'EDFAPAY_PARTNER'
- Add a permanent environment variable 'EDFAPAY_PARTNER=your partner code' example: EDFAPAY_PARTNER=636F64652D706172746E6572"

Below are the instruction to add permanent environment variable
----------------------------------------------------------------
MAC:
  Permanent environment variables are added to the .bash_profile file:
    1. Open the .bash_profile file with a text editor of your choice. (create file if not exist)
    2. Scroll down to the end of the .bash_profile file.
    3. Copy below text and paste to a new line. (replace `your partner code` with actual value received from `EdfaPay`)
         export EDFAPAY_PARTNER=your partner code
    4. Save changes you made to the .bash_profile file.
    5. Execute the new .bash_profile by either restarting the machine or running command below:
         source ~/.bash-profile

WINDOWS:
    1. Open the link below:
         https://phoenixnap.com/kb/windows-set-environment-variable#ftoc-heading-4
    2. Make sure below:
       - Variable name should be `EDFAPAY_PARTNER`
       - Variable value should be `your partner code` received from `EdfaPay`
"""
    )

    val invalidPartnerCode = Exception(
        """
Invalid partner code
- Please contact EdfaPay Administration or Sales for the correct partner code generated for your.
 - Contact Detail:
  - Email: info@edfapay.com/zohaib.kambrani@edfapay.com
  - Messenger: +966500409598 (WhatsApp Only) 
""")

    val invalidPartnerCodeToInstall = Exception(
        """
Invalid partner code of edfapay.softpos.install(mode:String, buildType:String, partnerCode:String)"
- Please contact EdfaPay Administration or Sales for the correct partner code generated for your.
 - Contact Detail:
  - Email: info@edfapay.com/zohaib.kambrani@edfapay.com
  - Messenger: +966500409598 (WhatsApp Only) 
""")

    val invalidInstallArgument = Exception("Missing/Invalid argument to edfapay.softpos.install(mode:String, buildType:String)")
}
REPO 
- SSCS SYA & MYA Performance Testing

==================================================================

DESCRIPTION
- This repo consists of both the SYA & MYA journeys

==================================================================

SYA JOURNEY
- User views Dashboard for draft cases
- Uploads a 2Mb file for evidence

==================================================================

MYA JOURNEY
- User clicks through the screen
- Uploads a 2Mb file for evidence

==================================================================

SYA DATA GENERATION

- 20%: New applications -  User is created. Complete a SYA journey
- 20%: 3 drafts - User is created. 3 x drafts are created to the SYA_120_DecidedIndependent transaction
- 30%: 10 drafts - User is created. 10 x drafts are created to the SYA_120_DecidedIndependent transaction
- 30%: 15 drafts - User is created. 15 x drafts are created to the SYA_120_DecidedIndependent transaction

This means that when the test starts, some users will Login, see no drafts and do the E2E journey, where as other 
will see a number of drafts (3 - 15), edit one and continue the rest of the journey

==================================================================

MYA DATA GENERATION

- Requires TYA numbers therefore, need to have cases that are submitted already
- Note - for MYA to work, an email (@mailnator domain) must be registered. This script does that

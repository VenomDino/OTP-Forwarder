# OTP-Forwarder

## How to Use This App?

### Step 1: Set Up Your Telegram Bot

1. **Create a Bot**: Use the **Bot Father** bot in Telegram to create a new bot and obtain the bot access token.

2. **Create a Private Group**: Set up a private group in Telegram and add the bot you created to this group.

3. **Assign Permissions**: Grant all necessary permissions to the bot within the group.

4. **Retrieve Group ID**: Get the ID of your group using the bot API. Visit `https://api.telegram.org/bot<YourBotToken>/getUpdates` and look for the `chat` object in the response. The `id` field in the `chat` object is your group ID.

### Step 2: Install and Configure OTP Forwarder

1. **Install the APK**: Download and install the OTP Forwarder APK on the target device.

2. **Grant Permissions**: Open the app and grant all required permissions.

3. **Enter Bot Details**: Fill in the input fields with your **bot token** and **chat ID**.

4. **Save Settings**: Click the **Save** button.

5. **You're Done!**: The app is now set up and ready to forward OTPs to your Telegram group.

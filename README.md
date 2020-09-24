# droid-jump
Sample game showcasing how to use Play Games Services in a game.

## Setup
 1. Create your project in the Google Play Developer Console.
 2. Link your app to the game configuration.
 3. Set the applicationId in build.gradle to the package name configured in the Google Play Developer Console.
 4. Copy the APP ID from the console into app/src/main/res/values/ids.xml.
 5. Copy your leaderboards and achievements data from the console into `app/src/main/res/values/ids.xml`

## Examples
1. **Sign In.** Allows to sign-in the user. You can find it in MainActivity.java and there are few methods for its integration:
	- `signInSilently` - for auto sign in.
	- `startSignInIntent` - for starting sign in process on button click.
2. **Achievements.** Allows players to see their locked and unlocked achievements. Files that contain achievements integration:
	- `AchievementsFragment` - for showing a custom UI with a list of achievements.
	- `AchievementDetailsFragment` - for showing a custom UI with information about a specific achievement.
	- AchievementsManager - for managing achievements data.
3. **Leaderboards.** Allows players to see their score and compare it with other players. Files that contain leaderboards integration:
	- `LeaderboardsFragment` - for showing a custom UI with a list of leaderboards.
	- `LeaderboardScoresFragment` - for showing a custom UI with scores of the leaderboard.
	- `ScoresManager` - for managing an achievements data.
4. **Friends.** Allows players to find their friends and compare game activity using in-game profile popup. The file that contains friends integration:
	- `FriendsFragment` - for showing a custom UI with a list of friends where the user can see all their friends and remove any person from the friend list.
5. **Video Recording** Helps easily add video recording to your game and lets users share their videos with friends on YouTube. Go to ``MainActivity`` and find method:
	- `showVideoOverlay` - displays video recording overlay.

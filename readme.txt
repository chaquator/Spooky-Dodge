TO COMPILE AND RUN FROM SOURCE:
-- In the root directory of the source code, open the shell and enter "gradlew run"

TO CREATE A COMPILED DISTRIBUTION:
-- In the root directory of the source code, open the shell and enter "gradlew distZip". The resulting distribution will be in build/distributions/spooky-dodge.zip

TO RUN FROM THE SUPPLIED COMPILED DISTRIBUTION:
-- In the spooky-dodge/bin directory of the distribution, run the "spooky-dodge" script correspoinding with your platform (Windows -> "spooky-dodge.bat", Linux/Mac/Unix -> "spooky-dodge")

GAME INSTRUCTIONS:
-- At any point on any screen, press Esc to exit the game.
-- From the title screen, press Enter to start the game.
-- In the in-game level, press use WASD, or the arrow keys, to move the ghost character
-- The objective of the game is to avoid the projectiles, they are marked as candy corn "bullets" with a white collision aura around them.
-- Your character has a small white circle in its center, this is the shape of your collision with the bullets; avoid colliding this shape with any bullet.
-- In the in-game level, press P to toggle and untoggle pause.
-- At the game over screen, press Enter to restart the in-game level, or T to return to the title srceen.
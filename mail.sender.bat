@echo off
SET ROOT_PATH=%CD%
java -cp .;%ROOT_PATH%\purplepinemusic.mail.jar; -Droot.path="%ROOT_PATH%" -Dlog4j.configurationFile="file:///%ROOT_PATH%\resources\log4j2.xml" com.purplepinemusic.Mailer
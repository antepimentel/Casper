# Casper

1. Install MySQL and create a new database

2. Create a new file called 'bot.properties'
  Use this template and fill in the required info.
  Place this in the root directory of the bot
  
  #TEMPLATE START
  DELIMITER=\!
  JDBC_USER=SQL USER FOT BOT
  JDBC_PASS=SQL PASSWORD FOR BOT
  JDBC_DB_NAME=SQL DB NAME
  #TEMPLATE END
  
3. Go to /casperdb and run db-create.sql in your MySQL command window

4. Run the bot from src/main.java/Core/Bot.java, or make a jar file and run that
   

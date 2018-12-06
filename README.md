# Casper

1. Install MySQL and create a new database
  1a. You may want to change the default wait_timeout variable for your mysql server or it might disconnect automatically.
      On Linux, go to /etc/mysql/my.cnf and add
      ```
      [mysqld]
      wait_timeout = 3153600
      interactive_timeout = 31536000
      ```
      These are in seconds and the default is 28800, which is 8 hours. 

2. Create a new file called 'bot.properties'
  Use this template and fill in the required info.
  Place this in the root directory of the bot
  ```
  DELIMITER=\!
  JDBC_USER=SQL USER FOT BOT
  JDBC_PASS=SQL PASSWORD FOR BOT
  JDBC_DB_NAME=SQL DB NAME
  BOT_KEY=YOUR DISCORD BOT KEY
  ```
3. Go to /casperdb and run db-create.sql in your MySQL command window

4. Run the bot from src/main.java/Core/Bot.java, or make a jar file and run that
   

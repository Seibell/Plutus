# 2022 Orbital
Team Plutus

# Level of Achievement
Apollo 11

# Project Motivation
When faced with tracking one’s expenditure and saving money, an individual could either not be bothered or not have the tools available to do so reliably. 

Many feel like it is a hassle to enter their expenditure and that they are alone in the journey of saving. However, we feel that an individual should understand their income and spending to have maximum control over their money.

# User Stories
Students mostly live off allowances from parents/guardians which is often not a large amount, hence they have to budget accordingly. With our mobile application, students can see their expenditures and the categories in which they spend the most. They can then take the necessary actions to cut down on expenditure in those categories.

Working Adults have to balance both income and expenditure, making sure they do not spend more than they earn (i.e expenditure > income). Thus they can enter their income for the month and track their daily expenditure to make sure the above does not occur. The mobile app can also send reminders when the user’s expenditure is 90% of their income to remind users to cut down on expenditure.

Students/Working Adults might forget to update their daily expenditure, at the end of every day, the mobile app will send reminders for users to enter their spending for the day unless this feature is turned off.

With our mobile app, users will be able to visualize their expenditure in terms of categories, or as a percentage of their income which makes saving more straightforward as the data is readily presented, hence no calculation is required on their part.

Users often feel like they are alone in their saving journey, as people don’t often talk about how much they save/how they save, the introduction of the leaderboards/friends system allows users to observe and even compete with their family/friends.

# Features
The Mobile App provides a platform for individuals to track their expenditures and incentivises them to save.

Registration and login
Dashboard - graphs, charts and expenditure reports
Income and daily expenditure input
Reminders for users to input income and daily expenditure
Goal and target setting
Tips and tricks pop-ups
Gamification System


# Development Plan
Milestone 1 (by 30 May)
In our milestone 1 submission, we created a base android application using Android Studio (Java) and Google Firebase. We included barebones features such as registration, login, and logout. In the future, we plan to implement other features such as password reset and additional layers of security via 2-factor authentication.
On the topic of account security, user information that is captured by the firebase console is hashed using a modified version of Scrypt. The console does not have access to user passwords, only the unique ID given to each account when it is created.
Registration feature: 
For this feature, we built upon the Firebase Authentication service and overwrote the createUserWithEmailAndPassword method provided, this allows us to create a unique user object with the information provided by the user: such as name and email.
Login feature: 
Similarly, this feature was also built with the aid of google firebase, we utilized the signInWithEmailAndPassword method and provided and authenticated users based on their email and password strings match the hashes in the firebase console.
Logout feature:
After successfully being authenticated and logging in, users will be prompted by a dashboard. We will plan to add the more unique features of our savings app in this dashboard for future milestones, but currently, it displays the user account's first and last name + email, along with the option to log out

Milestone 2 (by 27 June)
The core set of features should be completed.
User registration/login feature and integration with a database backend
Dashboard feature
Income and expenditure input + reminders
Goal and target setting

Milestone 3 (by 25 July)
Adding to additional features to improve QOL.
Gamification of app and rewards
Tips and tricks for saving
Add in ways for users to easily import past data (i.e. past transactions)
Advise users to cut spending in certain areas based on user expenditure data

# Tech Stack
Java
Android Studio
Firebase (User authentication / Database)
Figma (App Design)
Git (Version Control)


# Project Link
https://www.mediafire.com/file/t2vmlm1gi0ymrju/Plutus.apk/file

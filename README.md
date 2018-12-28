# Accounting Bootcamp

## Debug Database
Instructions for using [Android Debug Database](https://github.com/amitshekhariitbhu/Android-Debug-Database)

### Prerequisites
 * Must have Android Studio 3.0.0+
 * Must have Gradle 3.4+

### App Workflow
 * Starts in MainActivity.onCreate()
 * If network is available, load most recent quizzes from database
    * otherwise load pre-loaded from device database
 * MainActivity.displayQuizList() creates a QuizListFragment object
 * The QuizListFragment object uses the QuizListAdapter to display all quizzes in a RecyclerView
 * Each Quiz object in the RecyclerView has an onClickListener() to create a new QuizFragment
 
 ### Todo
    * Change the quiz to only count the first guess as either right or wrong, then allow to move on
        * Grey out other buttons and make them unclickable after first guess
    * Add a "results" page after quiz finishes
    * Add "View all quizzes" back button

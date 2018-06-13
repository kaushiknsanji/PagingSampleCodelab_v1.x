# Android Paging Sample

This App has been developed, following the instructions given in the Google Codelab [Android Paging](https://codelabs.developers.google.com/codelabs/android-paging/index.html#0). The original code by Google for this codelab can be referred [here](https://github.com/googlecodelabs/android-paging), which contains codes written in Kotlin. 

_**This Sample here has been rewritten in Java for Java Developers!**_

## What you will build

The App displays a list of Github Repositories using the [Github API](https://developer.github.com/v3/) for the search query entered. The query will be searched in the **Name** and **Description** fields of the Github repositories. The data is saved in the local database cache that is backed by the network data. As the user scrolls to the end of the displayed list, a new network request is triggered and the results are displayed and saved in the database.

This App sample here uses -
* **Room** - for local database cache.
* **LiveData** and **ViewModel** - for maintaining the data of the App.
* **DataBinding** - for binding View elements to their data. (Not present in the Google's Kotlin sample which employs Kotlin's features and Android KTX to accomplish the same)
* **[Retrofit](https://square.github.io/retrofit/)** and **[Okhttp](https://square.github.io/okhttp/)** - for communicating with the Github API.
* **Repository pattern** as per the [Guide to App Architecture](https://developer.android.com/topic/libraries/architecture/guide.html).
* **Java 8 lambdas**.

In this Codelab, one will learn how to integrate the [Paging Library](https://developer.android.com/topic/libraries/architecture/paging.html) components to accomplish the pagination task.

## Getting Started

* Android Studio 3.0 or higher
* Familiarity with Android Architecture Components and DataBinding(only for understanding purpose). 

### Branches in this Repository

* **[todo-starter-code](https://github.com/kaushiknsanji/PagingSampleCodelab/tree/todo-starter-code)**
    * This is the repository to start working with.
	* Contains codes written in Java.
	* Contains **TODOs** where one needs to write code. This is suitably marked such that a Java developer can follow with the existing Kotlin Codelab. 
	* Each Codelab step where one needs to write code, has been marked with the TODO comment following the syntax `//TODO(Step-X): ` where **`X`** is the Codelab Step number. This number may change if the original codelab changes.
* **[solution-code](https://github.com/kaushiknsanji/PagingSampleCodelab/tree/solution-code)**
    * This is the repository for the Solution code written in Java.
	
### Differences with respect to the Original Kotlin sample

* Firstly, the entire code has been rewritten in Java. Hence it uses Java 8 lambdas in places suitable for its usage.
* Binding View elements to their Data is carried out by DataBinding. In the Kotlin version, it uses the Kotlin features and Android KTX.
* Original version uses EditText for capturing User's Search query. In this sample, it is replaced with the `TextInputEditText` as per the recommendation for the `TextInputLayout` used.
* The String resources contains a Unicode character that is not interpretable in Android versions 5.0 or below. This is replaced with `%s` and the corresponding value is set in the Activity code that displays it.

## Sample Screenshot

<p align="center">
<img src="https://user-images.githubusercontent.com/26028981/41355036-2378ee92-6f3e-11e8-8a2a-d571479e7cdd.png" width="50%"/>
</p>

## References

References used for rewritting code in Java -
* [Paging Library (androidkt.com)](http://androidkt.com/paging-library/)
* [Rest API Pagination with Paging Library (androidkt.com)](http://androidkt.com/rest-api-pagination-paging-library/) 
* The Java version of Codelab written by [TROD-123](https://github.com/TROD-123/android-paging-java)

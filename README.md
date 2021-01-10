# Todo-list App

In this application user can add/delete/update items in TODO list. All the data is stored in Cloud Firestore. 

Tech stack: 
- Kotlin
- RxJava2
- LiveData
- Dagger2
- Paging 3
- Navigation component 
- Firestore
- Glide
- Leak Canary
- MVVM

## My thoughts:
- Firestore offline support does not work very well in my opinion.
Adding or editing items when there is no internet connection does not emit any values (success or error).
According to the Firebase documentation we should skip callback and proceed with flow which is not the best practise in my opinion.

- In this type of application I would rather use Dagger Hilt which is better for smaller apps and supports 
ViewModel injection, SavedStateHandle which took me a lot of time with Dagger

- RxJava is very powerfull tool when data needs to be transformed or filtered, which was not needed in this example so I should use Coroutines
which are much cleaner.


If I had to write the application again I would turn off Firestore persistence and create Room database for caching.
Then i would build RemoteMediator in which Room would be the source of truth and Firestore would be threated as a Backend Service.
What would be the resault of this?
## PROS
- Lower costs because users can edit single item multiple times (I would send to firebase only latest changes)
- Better offline support
+ Better refresh system
+ Better migration in case of changing Firestore to for example Backend Service. 

## CONS
- Writing second cache system when Firebase already created one.

Tests:
I have read tech requirements once and then focused on app requirements like was suggested in the recruitment task and i forgot about tests :(
Creating enviroment for testing viewModels and creating those test are huge time consumers.
And I did not want to make some dummy tests which would "assertThat(2==2)" thats why I decided to not post any of those.

Thanks for the opportunity to prove my skills, I've learned a lot from this :) 

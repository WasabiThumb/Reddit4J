# Reddit4J
An OAuth2 Reddit client for Java

## NOTICE
This project is in its very early stages and is missing many important features for a Reddit client. The main goal of this project is for browsing capability, and other things such as making posts
or comments may be added in the future.

## Usage
### Creating a RedditApplication instance
#### Using the default app instance
Using the default RedditApplication is quick and easy, and can be done in just one line.
```java
RedditApplication app = Reddit4J.getApplication();
```

#### Using your own app instance
First, you will need the app's client ID and in some cases the client secret. These can be obtained [here]((https://www.reddit.com/prefs/apps).
```java
RedditApplication app = new RedditApplication("CLIENT_ID");
// OR
RedditApplication app = new RedditApplication("CLIENT_ID", "CLIENT_SECRET");
```
The main advantage of this method is being able to control the branding shown on the OAuth screen, as well as change the port used from the default 8181.\
The app MUST have a redirect URL structured like so in order for local OAuth to work properly: ``http://127.0.0.1:port/``\
If you wish to use a non-standard port, you should change the system property ``r4j.oauth.port`` accordingly, either by doing
```java
System.setProperty("r4j.oauth.port", "12345");
```
or adding this launch option: ``-Dr4j.oauth.port=12345``

### Creating a RedditClient instance
#### Known bearer/refresh tokens
Once you have a RedditApplication instance, you can create a RedditClient like so:
```java
RedditClient rc = app.createClient("bearerToken");
// OR
RedditClient rc = app.createClient("bearerToken", "refreshToken");
```

#### Generate bearer/refresh tokens
A great feature of this library is the ability to do local OAuth. This method will launch a web server listening for the OAuth response and catch it immediately.\
The default behavior is to attempt to open the URL in the system default browser, however this behavior can be modified to fit your needs.\
```java
CompletableFuture<RedditClient> future = app.createClient(false, EnumSet.of(Scope.IDENTITY, Scope.READ));
```
Providing an explicit list of scopes is reccomended, as the default behavior is to grant ALL scopes which is often unecessary.\
Note that this supplies a CompletableFuture rather than a RedditClient, because it can take an arbitrary amount of time for it to resolve.\
If you want the RedditClient instance immediately, you can use ``future#get()``. It may throw errors, which can be caught by the superclass ``RedditOAuthException``.\
If you no longer need the auth server up (no more clients will be authenticated through OAuth2 at this time) then it is wise to close the server manually like so:
```java
Reddit4J.getOAuthServer().close();
```
Note that the OAuth server automatically re-launches whenever it is needed, so this action is not final.\
The bearer and refresh tokens (if present) can be extracted with ``RedditClient#getBearerToken()`` and ``RedditClient#getRefreshToken()``.

### Using the RedditClient
The client can now be used to accomplish various goals. You can see the JavaDocs for more information. Note that certain methods require certain scopes to be granted on the active session; however most things can be accomplished with the READ and IDENTITY scopes.\
The philosophy of this library is to provide a transparent layer over top of JSON APIs, so know that minimal caching is used and references are not typically actively held. This is due to the fact that Reddit's JSON structures are fairly irregular and not well documented, which is not a good match for Java.\
Due to this fact, classes that attempt to parse these JSON structures may not cover all possible properties of the structure and it may be necessary to use ``#getJSON()`` (defined by ``JsonObjectWrapper``) in order to perform more complex operations.

## Appendix
Knowledge of Reddit's API is reccomended for usage of this library.

Copyright 2022 Wasabi Codes

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

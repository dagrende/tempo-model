# Tempo-Model - Collaborative Temporal Model Editing

Experiments with collaborative modeling.

## Installation and running the web app

1. Install Eclipse Juno JEE
1. Install Google App Engine SDK, using `Help > Install New Software...` and the update site `https://dl.google.com/eclipse/plugin/4.2` (I checked al parts except GWT Designed for GPE, and NDK Plugns)
1. Clone this repository in eclipse using the `Import > Git > Projects from Git` in eclipse, resulting in the new project tempo-model
1. Add missing jar files, by using `New > Google > Web Application Project` and copying all files from its `war/WEB-INF/lib` into `tempo-model/war/WEB-INF/lib`
1. Compile GWT code with `Google > GWT Compile`
1. Start the web app by right clicking the project and select `Run As > Web Application`
1. Double click on the link that is visible in the Console view: <http://127.0.0.1:8888/Tempo.html?gwt.codesvr=127.0.0.1:9997>
1. Click Login and choose any id (doesn't have to be a valid google id in the test server)
1. After login the URL <http://127.0.0.1:8888> is opened, displaying the model editor
1. When developing the code, navigate to the <http://127.0.0.1:8888/Tempo.html?gwt.codesvr=127.0.0.1:9997> after login, to not have to GWT Compile for each change.

## Demo

* Click in the model editor to create new boxes, and see how new versions are created in the version tree below the editor
* Select the Delete tool and click on existing boxes to delete them
* Click on different versions to see them in the editor
* Create boxes when an old version is selected to create a version branch
* Open another browser window and log in as another user - look at the Participants view in each window (a bug may require you to move the mouse over both windows to see the updates of the participants views)
* Make changes in one window and look at both windows

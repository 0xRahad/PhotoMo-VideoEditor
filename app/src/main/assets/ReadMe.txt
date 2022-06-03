How to configure App Center Module in project:

step - 1 : Import app center module in project
            File -> New -> Import Module (Source path of app center module)
            
setp - 2 : Add module as dependency
            File -> Project structure -> app (module) -> dependencies -> + -> Module Dependency -> app center

step - 3 : Add in project build.gradle (app level)            
            
             android {
                packagingOptions {
                    exclude 'META-INF/DEPENDENCIES'
                }
             }
             
             dependencies {                  
                implementation 'com.karumi:dexter:5.0.0'  
                implementation 'com.google.android.gms:play-services-ads:18.0.0'
                implementation 'com.anjlab.android.iab.v3:library:1.0.44'
             }
             
setp - 4 : copy app_start package from app center module and paste in project

step - 5 : Add in project Manifest.xml

              <activity android:name=".app_start.SplashActivity">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
                        <action android:name="android.intent.action.VIEW" />
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
                </activity>
                <activity
                    android:name=".app_start.SplashHomeActivity"
                    android:screenOrientation="portrait" />
                    
step - 6 : If your project package name aplerady exist in package name them comment static package name
            SplashHomeActivity.java -> GetAdData task -> line no. 424
            
step - 7 : Open App center call HomePageActivity.class of app center module
            startActivity(new Intent(mContext, HomePageActivity.class));
            
step - 8 : change ads id with test id in strings.xml(app center module)



   
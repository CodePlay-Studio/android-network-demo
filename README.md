## android-network-demo
Source code that demonstrates the network operations in Android. Including:

**weather_v1 module** (Java)  
* Basic HTTP operation with HttpUrlConnection
* Run network operations in a worker / background thread using AsyncTask
* Implementing headless / model fragment which encapsulates the network operations in AsyncTask
* Implementation of a Volley singleton that encapsulates RequestQueue and other Volley functionality to perform various requests (e.g. the built-in JsonObjectRequest and the custom GsonRequest written by the author of Volley, Ficus Kirkpatrick)
* Demonstrate of different LruCache implementations (number or sizes of entries) for Volley's ImageLoader

**weather_v2 module** (Kotlin)
* Construct layout with ConstraintLayout.
* Using RecyclerView to display a list of options to select from.
* Enable View binding to direct references to all views that have an ID in the corresponding layout.
* Implementation of Retrofit2 library to perform simple Restful operation and the Moshi converter to convert the Json response string to Data class objects.
* Persist data with Shared Preferences (simple and small key-value pairs) and Room Persistence library (local database).

## License
This demo is developed to be used in Android Development training conducted by CodePlay Studio. It is lisenced under the Apache License, Version 2.0 (the "License"); You may not use this demo except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

The source code is provided "AS IS". Any expressed or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall the copyright holder be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (Including, but not limited to, procurement of goods or services; loss of use, data, or profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (Including negligence or otherwise) arising in any way out of the use of this source code, even if advised of the possibility of such damage.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the above copyright notice and disclaimer are retained in the redistributions of source code or reproduced in the documentation and/or other materials provided with the redistributions in binary form.

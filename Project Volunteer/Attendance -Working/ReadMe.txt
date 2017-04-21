Guys.The mainActivity for attendance testing works for now.

It is interacting with Varsha's db on a separate thing called "Event2". I created that so that I don't screw up the DB like I did before.

For changing it, we need to modify line 44

DatabaseReference myRef = database.getReference("Event2").child(evID).child("userList");

This connects to Event2 -> The Event ID that I need to connect -> userList in that eventID.


Rest all is simple Java and Android.

Cheers,

Arun Subramanian

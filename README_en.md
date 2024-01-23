### Prepared project
This project contains set of elements related to "hotel rooms booking" area:
- Client, Room, Booking, and RoomReservation entities.
- Screens (views):
    - CRUD for the Client,
    - CRUD for the Room,
    - Booking list view and edit(detail) view, as well as the Booking cancelling action.
    - BookingRoomAssignment - suitable room selection and assignment view.
    - ReservedRooms - list of already reserved rooms view.
- BookingService Spring bean, that contains stubs for logic to implement.

### TaskIn this homework you need to:
- extend the data model with some additional attributes
- implement the business logic stubs
- add several methods to maintain data consistency
- optimize data loading on one of the screens.

#### 1. Model validation
Add to existed model listed Bean Validation annotations:
- Client
    - email - correct email address string
    - telephone - a string of 8 to 16 characters long
- Room
    - number - an integer from 100 to 999
    - squareMeters - positive number
    - floor - an integer from 1 to 9

Start the application and check if validations are working as expected.

#### 2. Adding audit Traits
- Add to the Room entity new traits: Audit of creation and Audit of modification.
- Add to the rooms list data grid component (inside RoomListView screen) two additional columns for the added traits.
- Restart the application. Create and edit rooms through the user interface to see how these attributes are populated and modified.

#### 3. Calculated non-persistent attribute
- Add a new calculated attribute to the model and user interface - the countdown in days to the client's arrival.
- Add this new attribute to the Booking entity: name - countdownDays - an integer number. Use method approach for calculation
- Implement the calculation logic - how many days from now (today) until the client arrives (use arrivalDate attribute).
- Use @DependsOnProperties annotation for this method.
- Add calculated value as a new column to the BookingList and BookingAssignment views.

#### 4. Maintain data reliability using EntitySavingEvent
There is some denormalization of the data in the Booking entity: the attributes ArrivalDate, NightsOfStay and PurchaseDate are logically related. In this case, the “Departure Date” attribute is not filled in when creating a booking,
and can be automatically calculated and then used.

— Implement the calculation and saving of the value of the “departureDate” attribute. Use "arrivalDate" and "nightsOfStay" values. To do this, use EntitySavingEvent.
- Attribute value must be updated when a new Booking is created, as well when an existing Booking is changed.

#### 5. Composite instance name
By now you may have noticed that the Client entity does not have an instance name annotation.
Set up Client's composite instance name with formatting: "Firstname Lastname" (use existing attributes).

#### 6. Data loading using EntityManager
Implement the `com.company.flowhomeworkdataadvancedhotel.app.BookingService#isSuitable()` stub,
that must check if room is suitable for the Booking in the Room assignment screen.

Requirements:
- Check both of conditions below:
  1) Number of room's sleeping places (Room#sleepingPlaces) should not be less than the booking's number of guests (Booking#numberOfGuests).
  2) The selected room must not already be booked (there is no existing instance of RoomReservations) that overlaps between arrival and departure dates with the current Booking.
“NOTE” We accept that a new client can check into a room on the same day that the previous client left the room.
- Use the `javax.persistence.EntityManager` interface for the data loading.

You might want to check if your method implementation works as it should. To do this use Room assignment view (check the Assign column changes).

#### 7. Transaction for data manipulations
Implement the com.company.flowhomeworkdataadvancedhotel.app.BookingService#reserveRoom() method, which assigns the passed room to the passed reservation.

- The assignment itself is implemented by creating an instance of RoomReservation.
- Before the assignment, double-check your room and booking to ensure there have been no critical changes.
- Wrap both actions: check and assignment in one transaction to ensure isolation and atomicity of operations.
- You may use one of the transactional approaches: programmatic or declarative.

You may check the method's implementation in Room assignment view (use Assign action with suitable Room).
After assignment the room must be present in Reserved rooms screen.

#### 8. Model updating with EntityChangedEvent
Implement automatic room release (unassignment) if the corresponding Booking is cancelled.
The cancellation action is already implemented inside the BookingListView.

Requirements:
- When the Booking, that already has a room assigned (RoomReservation instance exists), changes status to Cancelled -
  then room must be released immediately.
- To do it it's enough to just delete related RoomReservation instance from database.
- Use `EntityChangedEvent` to achieve the solution.
- Room's freeing must be done in the same transaction, in which Booking's status is being changed.

#### 9. View fetch plan optimization. Meet and deal with "Cannot get unfetched attribute" error
Optimize the amount of data being loaded from database in Reserved rooms view, and learn how to deal with "unfetched attribute" error when attempting to access to attributes, that were not loaded.

The screen mentioned above contains some data related to RoomReservation and linked entities.

Fetch plan:
- Firstly, set logging level as "debug" for `logging.level.eclipselink.logging.sql` logger (in the application.properties file), so you are going to be able to see SQL queries in the app's console.
- Edit data container's fetchPlan. Decrease the number of attributes, in order not to load attributes that are not shown in data grid columns (e.g., Booking#status). Do it for all present entities.
- Verify by checking the SQL queries that the number of loaded attributes has been reduced.

Unfetched attribute error:
- Now try calling the implemented "View client email" method on one of the bookings of the table.
- Since fetch plans were changed, "unfetched attribute" error (exception) must be shown
  (because the email attribute is not present in a custom partial fetch plan, with which Client entities were loaded).
- Edit the code for the “View Client Email” action - `ReservedRoomsScreen.onRoomReservationsTableViewClientEmail()`,
 to keep the method running, but don't add the email attribute to the client's fetch plan (remember lazy loading at this point).

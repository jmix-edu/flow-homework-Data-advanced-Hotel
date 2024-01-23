### Prepared project
This project contains set of elements related to "hotel rooms reservation and booking" area:
- Client, Room, Booking, and RoomReservation entities.
- Screens (views):
    - CRUD for the Client,
    - CRUD for the Room,
    - Booking list view and edit(detail) view, as well as the Booking cancelling.
    - BookingRoomAssignment - suitable room selection and assignment view.
    - ReservedRooms - list of already reserved rooms view.
- BookingService Spring bean, that contains stubs for logic to implement.

### Task
In this homework you have to extend data model with some additional attributes, implement business logic stubs,
add some data consistence methods, and optimize data loading in one of the screens.

#### 1. Model validation
Add to existed model new Bean Validation annotations:
- Client
    - email - correct email address string
    - telephone - string from 8 to 16 symbols in length
- Room
    - number - number from 100 to 999
    - square meters - positive number
    - floor - number from 1 to 9

Launch the application and check, that validations are working as expected.

#### 2. Adding audit Traits
- Add to the Room entity new traits: Audit of creation and Audit of modification.
- Add to rooms list data grid component (inside RoomListView screen) two additional columns for added traits.
- Restart the application. Create and change new rooms via UI, to see how these attributes are filled and edited.

#### 3. Calculated non-persistent attribute
Add to the model and to the UI new calculated attribute - to show a booking countdown - countdown of client's arrival moment in days.
- Add this new attribute to the Booking entity: name - countdownDays - integer number. Use method approach for calculation
- Implement the calculation logic - how many days from now (today) until client arrives (use arrivalDate attribute).
- Use @DependsOnProperties annotation for this method.
- Add calculated value as a new column in BookingListView and BookingAssignment views.

#### 4. Maintain data reliability using EntitySavingEvent
There is some data denormalization in Booking entity: arrivalDate, nightsOfStay, and departureDate attributes are locally connected. At the same time, the departureDate attribute is not entered while booking creation,
and could be automatically calculated and then used.

- Realize departureDate attribute value calculating and saving using arrivalDate and nightsOfStay values.
- For achieve the task, use `EntitySavingEvent`.
- Attribute value must be updated when new Booking is created, as well when existed Booking is changed.

#### 5. Composite instance name
By the time you already might note, that Client entity does not have an instance name annotation.

Set up Client's composite instance name with formatting: "Firstname Lastname" (use existed attributes).

#### 6. Data loading using EntityManager
Implement the `com.company.flowhomeworkdataadvancedhotel.app.BookingService#isSuitable()`,
that must check if room is suitable for the Booking in Room assignment screen.

Requirements:
- Check both of conditions below:
  1) Number of room's sleeping places (Room#sleepingPlaces) must not be smaller than booking's number of guests (Booking#numberOfGuests).
  2) Selected room must not be already booked (no existed RoomReservations),
         that intersect in dates - from arrivalDate until departureDate with the current booking.
         We admit, that new client is able to check-inn to the room at the same day, as the previous client leaves the room.
- Use the `javax.persistence.EntityManager` interface to data loading.

You might like to check if your method implementation works as it should. To do so use Room assignment view (implemented rooms data grid Assign column).

#### 7. Transaction for data manipulations
Implement the `com.company.flowhomeworkdataadvancedhotel.app.BookingService#reserveRoom()` method, that assigns passed room to passed booking.

- Assignment itself is implemented by RoomReservation instance creation.
- Before the assignment, check the suitability between room and booking once again, to make sure that there are no breaking changes present.
- Wrap both actions: checking and assignment - ia a single transaction, to guarantee operations isolation and atomicity.
- You may use one of transactional approach: programmatic or declarative.

You may check the method's realisation in Room assignment view (use Assign action with suitable Room).
After assignment this room must be present in Reserved rooms screen.

#### 8. Model updating with EntityChangedEvent
Realise an automatic room freeing (un-assignment), if related Booking is cancelled.
Booking cancel action is implemented inside BookingListView.

Requirements:
- When the Booking, that already has a room assigned (RoomReservation), changes status to Cancelled -
  then room must be freed immediately.
- To do so it's enough to just delete related RoomReservation instance from database.
- Use `EntityChangedEvent` to achieve the solution.
- Room's freeing must be done in the same transaction, in which Booking's status is being changed.

#### 9. View fetch plan optimization. Meet and deal with "Cannot get unfetched attribute" error
Optimize the amount of data being loaded from database in Reserved rooms view, and learn how to deal with "unfetched attribute" error when attempting to access to attributes, that were not loaded.

The screen mentioned above shows some data related to RoomReservation and linked entities.

Fetch plan:
- Firstly, set logging level as "debug" for `logging.level.eclipselink.logging.sql` logger (application.properties file), so you are going to be able SQL queries in the app's console.
- Edit data container's fetchPlan. Decrease the number of attributes, in order not to load attributes that are not shown in data grid columns (e.g., Booking#status). Do so for all present entities.
- Make sure checking SQL query, that number of loaded attributes was lowered.

Unfetched attribute error:
- Now, try to call implemented method - "View client email" for one of the table's bookings.
- Since fetch plans were changed, "unfetched attribute" error (exception) must be shown
  (because the email attribute is not present in a custom partial fetch plan, with which Client entity was loaded).
- Redact "View client email" action's code - `ReservedRoomsScreen.onRoomReservationsTableViewClientEmail()`,
  so the method continues to work, but not add the email attribute to Client's fetch plan (remember about lazy loading at this point).
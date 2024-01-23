package com.company.flowhomeworkdataadvancedhotel.view.booking;

import com.company.flowhomeworkdataadvancedhotel.app.BookingService;
import com.company.flowhomeworkdataadvancedhotel.entity.Booking;

import com.company.flowhomeworkdataadvancedhotel.entity.Room;
import com.company.flowhomeworkdataadvancedhotel.entity.RoomReservation;
import com.company.flowhomeworkdataadvancedhotel.view.main.MainView;

import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "booking-room-assignment", layout = MainView.class)
@ViewController("BookingRoomAssignment.list")
@ViewDescriptor("booking-room-assignment.xml")
@LookupComponent("bookingsDataGrid")
@DialogMode(width = "64em")
public class BookingRoomAssignment extends StandardListView<Booking> {

    @ViewComponent
    private DataGrid<Booking> bookingsDataGrid;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private CollectionContainer<Room> roomsDc;
    @ViewComponent
    private CollectionContainer<Booking> bookingsDc;
    @ViewComponent
    private CollectionLoader<Room> roomsDl;

    private void confirmReserve(Room room, Booking booking) {
        dialogs.createOptionDialog()
                .withHeader("Please confirm")
                .withText("Reserve room #" + room.getNumber() + " to the booking?")
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(e -> {
                                    doReserveBooking(room, booking);
                                }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .open();
    }

    private void doReserveBooking(Room room, Booking booking) {
        RoomReservation result = bookingService.reserveRoom(booking, room);
        if (result == null) {
            notifications.create("Reserve failed")
                    .withType(Notifications.Type.ERROR)
                    .show();
            return;
        }
        roomsDc.getMutableItems().remove(room);
        bookingsDc.getMutableItems().remove(booking);
    }

    @Subscribe(id = "bookingsDc", target = Target.DATA_CONTAINER)
    public void onBookingsDcItemChange(InstanceContainer.ItemChangeEvent<Booking> event) {
        roomsDl.load();
    }

    @Supply(to = "roomDataGrid.assign", subject = "renderer")
    private Renderer<Room> roomDataGridAssignRenderer() {

        return new ComponentRenderer<>(room -> {

            Booking booking = bookingsDataGrid.getSingleSelectedItem();

            if (booking == null) {
                return null;
            }

            if (!bookingService.isSuitable(booking, room)) {
                return new H6("-");
            } else {

                JmixButton assignBtn = uiComponents.create(JmixButton.class);
                assignBtn.setText("Assign");
                assignBtn.addClickListener(e -> {
                    confirmReserve(room, booking);
                });
                return assignBtn;
            }
        });

    }

}
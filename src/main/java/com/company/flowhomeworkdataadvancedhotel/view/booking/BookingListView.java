package com.company.flowhomeworkdataadvancedhotel.view.booking;

import com.company.flowhomeworkdataadvancedhotel.entity.Booking;

import com.company.flowhomeworkdataadvancedhotel.entity.BookingStatus;
import com.company.flowhomeworkdataadvancedhotel.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "bookings", layout = MainView.class)
@ViewController("Booking.list")
@ViewDescriptor("booking-list-view.xml")
@LookupComponent("bookingsDataGrid")
@DialogMode(width = "64em")
public class BookingListView extends StandardListView<Booking> {

    @Autowired
    private Dialogs dialogs;
    @ViewComponent
    private DataGrid<Booking> bookingsDataGrid;
    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private CollectionContainer<Booking> bookingsDc;


    private void cancelBooking(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = dataManager.save(booking);
        bookingsDc.replaceItem(updated);
    }

    @Subscribe("bookingsDataGrid.cancel")
    public void onBookingsDataGridCancel(final ActionPerformedEvent event) {
        Booking booking = bookingsDataGrid.getSingleSelectedItem();
        if (booking == null) {
            return;
        }

        dialogs.createOptionDialog()
                .withHeader("Please confirm")
                .withText("The booking will be cancelled.")
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(e -> cancelBooking(booking)),
                        new DialogAction(DialogAction.Type.NO)
                )
                .open();
    }

    @Install(to = "bookingsDataGrid.cancel", subject = "enabledRule")
    private boolean bookingsDataGridCancelEnabledRule() {
        Booking booking = bookingsDataGrid.getSingleSelectedItem();
        return booking != null && booking.getStatus() == BookingStatus.BOOKED;
    }

}
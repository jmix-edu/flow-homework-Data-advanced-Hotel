package com.company.flowhomeworkdataadvancedhotel.view.booking;

import com.company.flowhomeworkdataadvancedhotel.entity.Booking;

import com.company.flowhomeworkdataadvancedhotel.entity.BookingStatus;
import com.company.flowhomeworkdataadvancedhotel.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "bookings/:id", layout = MainView.class)
@ViewController("Booking.detail")
@ViewDescriptor("booking-detail-view.xml")
@EditedEntityContainer("bookingDc")
public class BookingDetailView extends StandardDetailView<Booking> {
    @Subscribe
    public void onInitEntity(final InitEntityEvent<Booking> event) {
        event.getEntity().setStatus(BookingStatus.BOOKED);
    }
}
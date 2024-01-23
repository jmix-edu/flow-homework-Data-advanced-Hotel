package com.company.flowhomeworkdataadvancedhotel.view.room;

import com.company.flowhomeworkdataadvancedhotel.entity.Room;

import com.company.flowhomeworkdataadvancedhotel.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "rooms/:id", layout = MainView.class)
@ViewController("Room.detail")
@ViewDescriptor("room-detail-view.xml")
@EditedEntityContainer("roomDc")
public class RoomDetailView extends StandardDetailView<Room> {

}
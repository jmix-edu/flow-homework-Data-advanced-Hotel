package com.company.flowhomeworkdataadvancedhotel.view.client;

import com.company.flowhomeworkdataadvancedhotel.entity.Client;

import com.company.flowhomeworkdataadvancedhotel.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "clients/:id", layout = MainView.class)
@ViewController("Client.detail")
@ViewDescriptor("client-detail-view.xml")
@EditedEntityContainer("clientDc")
public class ClientDetailView extends StandardDetailView<Client> {
}
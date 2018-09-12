package com.stackroute.cvrp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.cvrp.domain.DateLogistics;
import com.stackroute.cvrp.domain.Location;
import com.stackroute.cvrp.domain.Order;
import com.stackroute.cvrp.domain.Route;
import com.stackroute.cvrp.domain.Slot;
import com.stackroute.cvrp.domain.Vehicle;

@Service
public class RoutingServiceImpl implements RoutingService {

	private CvrpServiceImpl1 cvrpServiceImpl1 = new CvrpServiceImpl1();

	// public RoutingServiceImpl(CvrpServiceImpl1 cvrpServiceImpl1) {
	// this.cvrpServiceImpl1=cvrpServiceImpl1;
	// }

	@Override
	public Route getOrderedRoute(Route route) {
		String slotId = null;
		int noOfVeh;
		int vehCap = 0;
		int noOfOrders = 0;
		Vehicle[] vehicles = null;
		Route routeObj = new Route();
		Slot[] slotArray = null;
		DateLogistics dateLogistics = new DateLogistics();
		List<Order> orders = new ArrayList<Order>();
		List<Location> locations = new ArrayList<Location>();
		double[][] distanceMatrix;
		cvrpServiceImpl1.setJson(route);
		
		Slot[] slots = cvrpServiceImpl1.getSlots();
		for (int i = 0; i < slots.length; i++) {
			slotId = slots[i].slotId;
			System.out.println(slots[i]);
			orders = cvrpServiceImpl1.getAllOrders(slotId);
			for (int k = 0; k < orders.size(); k++) {
				System.out.println("Orders " + orders.get(k));
			}
			noOfOrders = orders.size();
			locations = cvrpServiceImpl1.getAllLocationsBySlot(slotId);
			distanceMatrix = cvrpServiceImpl1.getDistanceMatrix(slotId);
			noOfVeh = Integer.parseInt(slots[i].getSlotNoOfVehicle());
			for (int j = 0; j < noOfVeh; j++) {
				vehCap = Integer.parseInt(slots[i].getSlotVehicle()[j].getVehicleCapacity());
			}
			CvrpServiceImpl1 cvrp = new CvrpServiceImpl1(noOfOrders, noOfVeh, vehCap);
			cvrp.greedySolution(orders.toArray(new Order[orders.size()]), distanceMatrix);
			cvrp.TabuSearch(10, distanceMatrix);
			vehicles = cvrp.updatedVehicles();
			slots[i].setSlotVehicle(vehicles);
			dateLogistics.setSlots(slots);
			routeObj.setDateLogistics(dateLogistics);
		}
		return routeObj;
	}
}

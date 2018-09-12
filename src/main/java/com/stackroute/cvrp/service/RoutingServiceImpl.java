package com.stackroute.cvrp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackroute.cvrp.domain.DateLogistics;
import com.stackroute.cvrp.domain.Location;
import com.stackroute.cvrp.domain.Order;
import com.stackroute.cvrp.domain.Route;
import com.stackroute.cvrp.domain.Slot;
import com.stackroute.cvrp.domain.Vehicle;

@Service
public class RoutingServiceImpl implements RoutingService {

	private CvrpServiceImpl1 cvrpServiceImpl1;
	private Order newOrder;
	private DateLogistics dateLogistics;
	private Slot[] slots;
	private Vehicle[] vehicles;
	private Order[] orders;
	private List<Order> ordersList;
	private List<Location> locationList;
	private Location location;
	private Location newOrderLocation;
	private Location depoLocation=new Location("12.9353863", "77.6117461");
	private double[][] distanceMatrix;
	

	@Autowired
	public RoutingServiceImpl(CvrpServiceImpl1 cvrpServiceImpl1) {
		this.cvrpServiceImpl1 = cvrpServiceImpl1;
	}

	public void convertToJson(Object obj) {

		ObjectMapper mapperObj = new ObjectMapper();
		try {
			// get Employee object as a json string
			String jsonStr = mapperObj.writeValueAsString(obj);
			System.out.println(jsonStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Route getNewOrderedRoute(Route route) {
		System.out.println("route is " + route);
		newOrder = route.getNewOrder();
		dateLogistics = route.getDateLogistics();
		slots = dateLogistics.getSlots();
		ordersList = new ArrayList<>();
		locationList = new ArrayList<>();
		locationList.add(depoLocation);
		for (int i = 0; i < slots.length; i++) {
			vehicles = slots[i].getSlotVehicle();
			for (int j = 0; j < vehicles.length; j++) {
				orders = vehicles[i].getVehicleRoute();
				if (orders != null) {
					for (int k = 0; k < orders.length; k++) {
						ordersList.add(orders[k]);
					}
				}
			}
			
//			System.out.println("oroderList is " + ordersList);
			for (int l = 0; l < ordersList.size(); l++) {
				location = ordersList.get(l).getOrderLocation();
				locationList.add(location);
			}

		}
		newOrderLocation=newOrder.getOrderLocation();
		ordersList.add(newOrder);
		locationList.add(newOrderLocation);
		distanceMatrix=cvrpServiceImpl1.getDistanceMatrix(locationList);
//		cvrpServiceImpl1.checkIfFits(newOrder.getOrderVolume());
//		cvrpServiceImpl1.greedySolution(ordersList, distanceMatrix);
		
		for (int i = 0; i < distanceMatrix.length; i++) {
			for (int j = 0; j < distanceMatrix.length; j++)
				System.out.println("distance matrix is " + distanceMatrix[i][j]);
		}

		System.out.println("orderList fianl is " + ordersList);
		System.out.println("");
		System.out.println("LocationList " + locationList);
		convertToJson(ordersList);

		return null;
	}

//	@Override
//	public Route getOrderedRoute(Route route) {
//
//		System.out.println("In RoutingServiceImpl ->>>>>>>>>");
//		// System.out.println("Route is " + route + "\n");
//		String slotId = null;
//		int noOfVeh;
//		int vehCap = 0;
//		int noOfOrders = 0;
//		Vehicle[] vehicles = null;
//		Route routeObj = new Route();
//		Slot[] slotArray = null;
//		DateLogistics dateLogistics = new DateLogistics();
//		List<Order> orders = new ArrayList<Order>();
//		List<Location> locations = new ArrayList<Location>();
//		double[][] distanceMatrix;
//		System.out.println("json is jscnhk" + route);
//		cvrpServiceImpl1.setJson(route);
//		Route routeObj1 = cvrpServiceImpl1.getJson();
//		// System.out.println("routing route 111"+routeObj1);
//		DateLogistics dateLog = cvrpServiceImpl1.getDateLogistics();
//		System.out.println("date logistics in routingservice" + dateLog);
//		// cvrpServiceImpl1.se
//		routeObj1.setDateLogistics(dateLogistics);
//		Slot[] slots = dateLog.getSlots();
//		for (int j = 0; j < slots.length; j++) {
//			System.out.println(slots[j]);
//		}
//		for (int i = 0; i < slots.length; i++) {
//			slotId = slots[i].slotId;
//			System.out.println(slots[i]);
//			orders = cvrpServiceImpl1.getAllOrders(slotId);
//
//			for (int k = 0; k < orders.size(); k++) {
//				System.out.println("Orders " + orders.get(k));
//				orders.get(k).setRouted(false);
//			}
//			noOfOrders = orders.size();
//			locations = cvrpServiceImpl1.getAllLocationsBySlot(slotId);
//			distanceMatrix = cvrpServiceImpl1.getDistanceMatrix(slotId);
//			noOfVeh = Integer.parseInt(slots[i].getSlotNoOfVehicle());
//			for (int j = 0; j < noOfVeh; j++) {
//				vehCap = Integer.parseInt(slots[i].getSlotVehicle()[j].getVehicleCapacity());
//			}
//			CvrpServiceImpl1 cvrp = new CvrpServiceImpl1(noOfOrders, noOfVeh, vehCap);
//			System.out.println("Start of greedy solution");
//			// =cvrp.getJson();
//			System.out.println("json in routing" + routeObj);
//			// cvrp.getDateLogistics();
//			cvrp.greedySolution(orders.toArray(new Order[orders.size()]), distanceMatrix);
//			System.out.println("end of greedy solution");
//			cvrp.TabuSearch(10, distanceMatrix);
//			vehicles = cvrp.updatedVehicles();
//			System.out.println("updated vehicle route" + vehicles[i].getVehicleRoute());
//			slots[i].setSlotVehicle(vehicles);
//			dateLogistics.setSlots(slots);
//			routeObj.setDateLogistics(dateLogistics);
//		}
//		return routeObj;
//	}
}

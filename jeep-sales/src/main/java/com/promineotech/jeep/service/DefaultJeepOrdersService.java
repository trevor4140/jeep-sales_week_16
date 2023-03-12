package com.promineotech.jeep.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.promineotech.jeep.dao.JeepOrderDao;
import com.promineotech.jeep.entity.Color;
import com.promineotech.jeep.entity.Customer;
import com.promineotech.jeep.entity.Engine;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.Option;
import com.promineotech.jeep.entity.Order;
import com.promineotech.jeep.entity.OrderRequest;
import com.promineotech.jeep.entity.Tire;
@Service
public class DefaultJeepOrdersService implements JeepOrderService{

	@Autowired 
	private JeepOrderDao jeepOrderDao;

	@Transactional
	@Override
	public Order createOrder(OrderRequest orderRequest) {
		Customer customer = getCustomer(orderRequest);
	    Jeep model = getModel(orderRequest);
	    Color color = getColor(orderRequest);
	    Engine engine = getEngine(orderRequest);
	    Tire tire = getTire(orderRequest);
	    List<Option> options = getOption(orderRequest);
	    //@formatter:off
	    BigDecimal price = options
	        .stream()
	        .map(Option::getPrice)
	        .reduce(BigDecimal.ZERO, BigDecimal::add)
	        .add(model.getBasePrice())
	        .add(tire.getPrice())
	        .add(engine.getPrice())
	        .add(color.getPrice());
	    //@formatter:on
		
		return jeepOrderDao.saveOrder(customer, model, color, engine, tire, price, options);
	}
	/**
	   * 
	   * @param orderRequest
	   * @return
	   */
	  private List<Option> getOption(OrderRequest orderRequest) {
	    return jeepOrderDao.fetchOptions(orderRequest.getOptions());
	  }

	  /**
	   * 
	   * @param orderRequest
	   * @return
	   */
	  private Tire getTire(OrderRequest orderRequest) {
	    return jeepOrderDao.fetchTire(orderRequest.getTire())
	        .orElseThrow(() -> new NoSuchElementException(
	            "Tire with ID=" + orderRequest.getTire() + " was not found"));
	  }

	  /**
	   * 
	   * @param orderRequest
	   * @return
	   */
	  private Engine getEngine(OrderRequest orderRequest) {
	    return jeepOrderDao.fetchEngine(orderRequest.getEngine())
	        .orElseThrow(() -> new NoSuchElementException(
	            "Engine with ID=" + orderRequest.getEngine() + " was not found"));
	  }

	  /**
	   * 
	   * @param orderRequest
	   * @return
	   */
	  private Color getColor(OrderRequest orderRequest) {
	    return jeepOrderDao.fetchColor(orderRequest.getColor())
	        .orElseThrow(() -> new NoSuchElementException(
	            "Color with ID=" + orderRequest.getColor() + " was not found"));
	  }

	  /**
	   * 
	   * @param orderRequest
	   * @return
	   */
	  private Jeep getModel(OrderRequest orderRequest) {
	    return jeepOrderDao
	        .fetchModel(orderRequest.getModel(), orderRequest.getTrim(),
	            orderRequest.getDoors())
	        .orElseThrow(() -> new NoSuchElementException("Model with ID="
	            + orderRequest.getModel() + ", trim=" + orderRequest.getTrim()
	            + orderRequest.getDoors() + " was not found"));
	  }

	  /**
	   * 
	   * @param orderRequest
	   * @return
	   */
	  private Customer getCustomer(OrderRequest orderRequest) {
	    return jeepOrderDao.fetchCustomer(orderRequest.getCustomer())
	        .orElseThrow(() -> new NoSuchElementException("Customer with ID="
	            + orderRequest.getCustomer() + " was not found"));
	  }

}

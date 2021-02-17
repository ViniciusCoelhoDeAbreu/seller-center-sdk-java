package com.rocketlabs.sellercenterapi.entities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.jcabi.aspects.Loggable;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.BaseRequest;
import com.rocketlabs.sellercenterapi.core.Client;
import com.rocketlabs.sellercenterapi.core.request.Request;
import com.rocketlabs.sellercenterapi.core.response.SuccessResponse;
import com.rocketlabs.sellercenterapi.exceptions.SdkException;

/**
 * Interface responsible to gather generic calls (not related to a special entity)
 * and configuration (dynamic)
 */
public final class SellerCenter {

    /**
     * The URL of the Seller Center API
     */
    public static String url;

    /**
     * The API key of the User
     */
    public static String apiKey;

    /**
     * The ID of the User
     */
    public static String userId;

    /**
     * The version of the API
     */
    public static String version = "1.0";

    /**
     * Repository to fetch/save orders
     */
    private static final OrderRepository orderRepository = new OrderRepository();

    /**
     * Repository to fetch/save products
     */
    private static final ProductRepository productRepository = new ProductRepository();

    /**
     * Retrieve list of orders from repository
     *
     * @param options optional request parameters
     * @return list of the customer details for a range of orders
     * @throws SdkException
     */
    
    static {
    	Annotation annotation = ApacheRequest.class.getDeclaredAnnotations()[1];
    	Annotation annotation2 = BaseRequest.class.getDeclaredAnnotations()[1];
    	changeAnnotationValue(annotation, "value", Loggable.ERROR);
    	changeAnnotationValue(annotation2, "value", Loggable.ERROR);
    	
    }
    
    @SuppressWarnings("unchecked")
	public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue){
	    Object handler = Proxy.getInvocationHandler(annotation);
	    Field f;
	    try {
	        f = handler.getClass().getDeclaredField("memberValues");
	    } catch (NoSuchFieldException | SecurityException e) {
	        throw new IllegalStateException(e);
	    }
	    f.setAccessible(true);
	    Map<String, Object> memberValues;
	    try {
	        memberValues = (Map<String, Object>) f.get(handler);
	    } catch (IllegalArgumentException | IllegalAccessException e) {
	        throw new IllegalStateException(e);
	    }
	    Object oldValue = memberValues.get(key);
	    if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
	        throw new IllegalArgumentException();
	    }
	    memberValues.put(key,newValue);
	    return oldValue;
	}
    
    public static OrderCollection getOrders(GetOrdersOptions options) throws SdkException {
        return orderRepository.retrieve(options);
    }

    /**
     * Retrieve feed count data from Seller Center
     *
     * @return feed statistics
     * @throws SdkException
     */
    public static FeedCount feedCount() throws SdkException {
        SuccessResponse response = Client.call(
                new Request("FeedCount", userId, apiKey, version)
        );

        return new FeedCount(response);
    }

    /**
     * Retrieve list of products from repository
     *
     * @param options optional request parameters
     * @return list of all or a range of products
     * @throws SdkException
     */
    public static ProductCollection getProducts(GetProductsOptions options) throws SdkException {
        return productRepository.retrieve(options);
    }

    /**
     *
     * @return
     * @throws SdkException
     */
    public static Collection<Category> getCategoryTree() throws SdkException {
        return productRepository.getCategoryTree();
    }

    /**
     *
     * @param products
     * @throws SdkException
     */
    public static void createProduct(List<Map> products) throws SdkException {
        productRepository.createProduct(products);
    }


    /**
     * Returns additional error context for SetToCancelled and SetToFailedDelivery
     *
     * @return A collection of reasons to provide
     * @throws SdkException
     */
    public static ReasonCollection getFailureReasons() throws SdkException {
        return orderRepository.getFailureReasons();
    }

    /**
     * Returns a list of all active shipping providers for SetStatusToShipped.
     *
     * @return A collection of providers
     * @throws SdkException
     */
    public static ShipmentProviderCollection getShipmentProviders() throws SdkException {
        return orderRepository.getShipmentProviders();
    }
}

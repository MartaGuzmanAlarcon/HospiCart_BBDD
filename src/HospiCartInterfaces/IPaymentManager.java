package HospiCartInterfaces;
import java.sql.SQLException;
import java.util.List;

import Exceptions.ClientException;
import Exceptions.OrderExceptions;
import HospiCartPOJOs.Payment;
import HospiCartPOJOs.PaymentMethod;
import HospiCartPOJOs.PaymentStatus;

/**
 * The interface IPaymentManager includes all CRUD operations and business methods for Payment entities.
 */
public interface IPaymentManager {
	public void insertPayment(Payment p) throws SQLException;
	public void deletePaymentById(Integer paymentId) throws Exception;
	
	public List<Payment> getListOfPayments();
	public Payment getPaymentById(Integer p_id);
	
	// TODO REVISE WHICH METHODS ARE REDUNDANT
	public void updateAmount(Integer paymentId, Integer amount) throws Exception;
	public void updatePaymentMethod(Integer paymentId, PaymentMethod method) throws Exception;
	public void updatePaymentStatus(Integer paymentId, PaymentStatus status) throws Exception;
	
	
	public Payment getPaymentByOrderId(int order_id) throws ClientException, OrderExceptions;
}

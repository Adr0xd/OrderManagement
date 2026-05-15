package org.mishu.Business;

import org.mishu.DataAccess.Wrappers.BillDBWrapper;
import org.mishu.Model.Bill;

import java.util.List;
import java.util.NoSuchElementException;

public class BillBLL {

    private final BillDBWrapper dbWrapper;

    public BillBLL() {
        dbWrapper = new BillDBWrapper(Bill.class);
    }

    public List<Bill> findAllBills() {
        return dbWrapper.findAll();
    }

    public Bill findBillByOrderId(int orderId) {
        Bill b = dbWrapper.findByOrderId(orderId);
        if (b == null) {
            throw new NoSuchElementException("No bill for orderId=" + orderId);
        }
        return b;
    }
}

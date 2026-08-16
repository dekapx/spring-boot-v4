# Cancellations & Returns Policy

## Cancelling an Order
Orders can only be cancelled while in CREATED, CONFIRMED, or PROCESSING status. Once an
order is SHIPPED it cannot be cancelled directly; the customer must wait for delivery and
then initiate a return instead. When an order is cancelled, the `cancellationReason` field
should be populated with a short human-readable reason (e.g. "Customer requested",
"Out of stock", "Payment failed").

## Returns
Returned items are tracked by moving the order status to RETURNED. Refund processing time
is typically 5-7 business days after the returned item is received at the warehouse.

## Agent Guidance
When a customer asks to cancel an order, the agent should explain the current policy above
and, if cancellation is not possible because the order already shipped, offer to help them
track the shipment or start a return once it is delivered instead.

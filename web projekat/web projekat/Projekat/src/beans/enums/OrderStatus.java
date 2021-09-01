package beans.enums;

import java.io.Serializable;

public enum OrderStatus implements Serializable {
	IN_PROCESSING, IN_PREPARATION, ON_HOLD, SENT, DELIVERED, REJECTED, REQUEST_SENT
}

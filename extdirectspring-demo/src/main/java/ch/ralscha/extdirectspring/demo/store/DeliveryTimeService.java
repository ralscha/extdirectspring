package ch.ralscha.extdirectspring.demo.store;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class DeliveryTimeService {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "combobox")
	public DeliveryTime[] getDeliveryTimes() {
		return DeliveryTime.values();
	}
}

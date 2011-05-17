package ch.ralscha.extdirectspring.demo.store;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Service
public class DeliveryTimeService {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "combobox")
	public List<DeliveryTime> getDeliveryTimes() {
		return Arrays.asList(DeliveryTime.values());
	}
}

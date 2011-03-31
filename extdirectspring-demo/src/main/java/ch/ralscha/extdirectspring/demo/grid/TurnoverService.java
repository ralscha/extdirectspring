package ch.ralscha.extdirectspring.demo.grid;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.ImmutableList;

@Service
public class TurnoverService {

	private ImmutableList<Company> companies;

	public TurnoverService() {
		ImmutableList.Builder<Company> builder = new ImmutableList.Builder<Company>();

		builder.add(new Company("ABC Accounting", 50000));
		builder.add(new Company("Ezy Video Rental", 106300));
		builder.add(new Company("Greens Fruit Grocery", 120000));
		builder.add(new Company("Icecream Express", 73000));
		builder.add(new Company("Ripped Gym", 88400));
		builder.add(new Company("Smith Auto Mechanic", 222980));

		companies = builder.build();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "turnover")
	public List<Company> getTurnovers() {
		return companies;
	}

}

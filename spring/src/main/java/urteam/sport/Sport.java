package urteam.sport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sport {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(unique = true)
	private String name;
	
	private float multiplicator;
	
	
	public Sport(){}

	public Sport(String name, float multiplicator) {
		super();
		this.name = name;
		this.multiplicator = multiplicator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getMultiplicator() {
		return multiplicator;
	}

	public void setMultiplicator(float multiplicator) {
		this.multiplicator = multiplicator;
	}
	
	

}

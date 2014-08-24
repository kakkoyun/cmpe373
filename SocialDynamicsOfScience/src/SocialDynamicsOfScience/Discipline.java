package SocialDynamicsOfScience;

/**
 * Discipline represents scientific discipline which is related with author and papers.
 *
 * There are three elements in the SDS model: papers, authors, and disciplines.
 * It represents a social network among authors.
 * The social network starts with one author writing one paper in one discipline.
 * The network then evolves as new authors join, new papers are written, and new disciplines emerge over time.
 *
 * @author Kemal Akkoyun $ 11076004
 * @version V1.0 - Alpha
 * @category Cmpe373 - Term Project
 * @see ISDoS
 * 
 */
public class Discipline implements ISDoS{
	/**
	 * Discipline instances identified by a unique integer.
	 */
	private Integer id;

	/**
	 * Constructor for class.
	 * @param id A unique identifier for a discipline.
	 */
	public Discipline(Integer id) {
		this.id = id;
	}
	
	// Getters and setters.
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}

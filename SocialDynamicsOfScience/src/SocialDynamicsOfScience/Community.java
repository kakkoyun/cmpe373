package SocialDynamicsOfScience;

import java.util.ArrayList;

/**
 * Community - Community is a cluster of authors.
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

public class Community implements ISDoS {
	/**
	 * Community instances identified by a unique integer.
	 */
	private int id;
	/**
	 * List of authors belong to community.
	 * @see Author
	 */
	private ArrayList<Author> authors = new ArrayList<Author>();
	/**
	 * List of papers between community's authors.
	 */
	private ArrayList<Paper> papers = new ArrayList<Paper>();
	
	public Community(){}
	
	/**
	 * Constructor for class.
	 * 
	 * @param id A unique identifier for a community.
	 * @param authors List of authors.
	 * @param papers List of papers.
	 * 
	 */
	public Community(int id, ArrayList<Author> authors, ArrayList<Paper> papers) {
		this.id = id;
		this.authors = authors;
		this.papers = papers;
	}
	
	// Getters and setters.
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(ArrayList<Author> authors) {
		this.authors = authors;
	}
	public ArrayList<Paper> getPapers() {
		return papers;
	}
	public void setPapers(ArrayList<Paper> papers) {
		this.papers = papers;
	}
	
}

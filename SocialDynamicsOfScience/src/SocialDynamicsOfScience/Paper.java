package SocialDynamicsOfScience;

import java.util.ArrayList;
import org.aspectj.weaver.ISourceContext;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

/**
 * Paper is relation between authors, edge between authors in co-authorship network.
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
public class Paper implements ISDoS{
	/**
	 * Paper instances identified by a unique integer.
	 */
	private Integer id;
	/**
	 * A list of coAuthors for paper.
	 * @see Author
	 */
	private ArrayList<Author> authors = new ArrayList<Author>();
	/**
	 * A list of disciplines for paper.
	 * @see Paper
	 */
	private Discipline discipline;
	/**
	 * We propose a simple mechanism to model knowledge diffusion through collaboration in SDS: 
	 * when authors write a paper together, they all contribute their knowledge.
	 * Therefore, a paper inherits the union of the author disciplines as topics. 
	 * However, the discipline that is shared by the majority of authors is selected as the main topic of the paper;
	 * (say, the publication venue) and diffuses across all the authors. 
	 * Through the collaboration, authors acquire knowledge of and membership in this area.
	 * unionOfSharedDiscipline: A list of diffused knowledge through paper.
	 * @see Discipline
	 * 
	 */ 
	private ArrayList<Discipline> unionOfSharedDisciplines = new ArrayList<Discipline>();
	
	/**
	 * Constructor of class.
	 * @param id
	 */
	public Paper(Integer id) {
		this.id = id;
	}

	/**
	 * Constructor of class.
	 * @param authors List of authors that paper written by.
	 * @param discipline Discipline that paper written in.
	 * @param unionOfSharedDisciplines List of disciplines, contains union of paper's authors disciplines.
	 */
	public Paper(Integer id, ArrayList<Author> authors, Discipline discipline,
			ArrayList<Discipline> sharedDisciplines) {

		this.id = id;
		this.authors = authors;
		this.discipline = discipline;
		this.unionOfSharedDisciplines = sharedDisciplines;
	}
	
	// Getters and setters.
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public ArrayList<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(ArrayList<Author> authors) {
		this.authors = authors;
	}
	public Discipline getDiscipline() {
		return discipline;
	}
	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}

	public ArrayList<Discipline> getSharedDisciplines() {
		return unionOfSharedDisciplines;
	}

	public void setSharedDisciplines(ArrayList<Discipline> sharedDisciplines) {
		this.unionOfSharedDisciplines = sharedDisciplines;
	}
	/**
	 * 
	 * We propose a simple mechanism to model knowledge diffusion through collaboration in SDS: 
	 * when authors write a paper together, they all contribute their knowledge.
	 * Therefore, a paper inherits the union of the author disciplines as topics. 
	 * However, the discipline that is shared by the majority of authors is selected as the main topic of the paper;
	 * (say, the publication venue) and diffuses across all the authors. 
	 * Through the collaboration, authors acquire knowledge of and membership in this area.
	 * unionOfSharedDiscipline: A list of diffused knowledge through paper.
	 * 
	 * @param context Context of paper agent that it belong.
	 * @see Discipline
	 */
	public void decideDiscipline(Context<ISDoS> context){
		
		IndexedIterable<ISDoS> allDisciplines = context.getObjects(Discipline.class);
		int[] disciplineCount = new int[allDisciplines.size()];
		for(Discipline d : unionOfSharedDisciplines){
			disciplineCount[d.getId()] ++;
		}
		// For this stage only one discipline is set for a paper.
		int mostlySharedDisciplineID = 0;
		for(int i = 0; i < disciplineCount.length; i++){
			if(disciplineCount[i] > mostlySharedDisciplineID){
				mostlySharedDisciplineID = i;
			}
		}
		 setDiscipline((Discipline)allDisciplines.get(mostlySharedDisciplineID));
	}
	
	/**
	 * We propose a simple mechanism to model knowledge diffusion through collaboration in SDS: 
	 * when authors write a paper together, they all contribute their knowledge.
	 * Therefore, a paper inherits the union of the author disciplines as topics. 
	 * However, the discipline that is shared by the majority of authors is selected as the main topic of the paper;
	 * (say, the publication venue) and diffuses across all the authors. 
	 * Through the collaboration, authors acquire knowledge of and membership in this area.
	 * unionOfSharedDiscipline: A list of diffused knowledge through paper.
	 * @see Discipline
	 * 
	 */
	public void updateDisciplinesOfAuthors(){
		for(Author author : authors){
			if(!author.getDisciplines().contains(discipline)){
				author.getDisciplines().add(discipline);
			}
		}
	}
	
	/**
	 * We propose a simple mechanism to model knowledge diffusion through collaboration in SDS: 
	 * when authors write a paper together, they all contribute their knowledge.
	 * Therefore, a paper inherits the union of the author disciplines as topics. 
	 * However, the discipline that is shared by the majority of authors is selected as the main topic of the paper;
	 * (say, the publication venue) and diffuses across all the authors. 
	 * Through the collaboration, authors acquire knowledge of and membership in this area.
	 * unionOfSharedDiscipline: A list of diffused knowledge through paper.
	 * @see Discipline
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void updateCoAuthorNetwork(){
		Context<ISourceContext> context = ContextUtils.getContext(this);
		Network<ISDoS> net = (Network<ISDoS>)context.getProjection("coAuthorship network");
		System.out.println("Authors:" + authors.size());
		for(int i = 0; i < authors.size(); i++){
			for(int j = i+1; j < authors.size(); j++){
				System.out.println("Added "+authors.get(i).getId()+" "+authors.get(j).getId());
				RepastEdge<ISDoS> edge = net.getEdge(authors.get(i), authors.get(j));
				if(edge == null){
					net.addEdge(authors.get(i), authors.get(j));
				} else {
					edge.setWeight(edge.getWeight()+1);
				}
			}
		}
	}
	
}

	
package SocialDynamicsOfScience;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

/**
 * Author is acting agent in simulation.
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
public class Author implements ISDoS{
	
	/**
	 * Author instances identified by a unique integer.
	 */
	private int id;
	/**
	 * A list of Disciplines that represents background knowledge of an author.
	 * @see Discipline 
	 */
	private ArrayList<Discipline> disciplines = new ArrayList<Discipline>();
	/**
	 * A list of Papers that certain Author had written.
	 * @see Paper
	 */
	private ArrayList<Paper> papers = new ArrayList<Paper>();
	/**
	 * Represents community that author belongs.
	 * @see Community
	 */
	private Community community;

	public Author() {}

	/**
	 * Constructor for class.
	 * 
	 * @param id A unique identifier for an author.
	 */
	public Author(int id) {
		this.id = id;
		this.setCommunity(new Community());
	}
	
	// Getters and setters.

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Discipline> getDisciplines() {
		return disciplines;
	}
	public void setDisciplines(ArrayList<Discipline> disciplines) {
		this.disciplines = disciplines;
	}
	public ArrayList<Paper> getPapers() {
		return papers;
	}
	public void setPapers(ArrayList<Paper> papers) {
		this.papers = papers;
	}
	
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	/**
	 * A step in a biased random walk.
	 * We model these behaviors through a biased random walk. 
	 * The length of the random walk determines the number of co-authors. 
	 * At each step in the walk, the author visits a node (starting with itself) and decides to stop with probability pW , or to search for additional authors with probability (1-pW) .
	 * In modeling the choice of collaborators, we aim to capture a few basic intuitions:
	 * (i) authors who have collaborated before are likely to do so again; 
	 * (ii) authors with common collaborators are likely to collaborate with each other;
	 * (iii) it is easier to choose collaborators with similar than dissimilar background;
	 * (iv) authors with many collaborations have higher probability to gain additional ones.
	 * 
	 * In the latter case a neighbor is selected as co-author according to the transition probability.
	 * Note that the walk may result in a single author.
	 * 
	 * @param pW Probability of random walk to continue as it is explained above.
	 * @param paper Thus a biased random walk represents co-authors of a paper, paper needs to be passed through walk.
	 * @see SocialDynamicsOfScienceBuilder
	 */
	@SuppressWarnings("unchecked")
	public void step(Double pW, Paper paper) {
		
		/**
		 * Valid but deprecated method for probabilistic distribution of walk,
		 * TODO: Fix this in later mature versions.	 
		 RandomHelper.createBinomial(context.getNumberOfPapers, pW);
		 Binomial binomialDistribution = RandomHelper.getBinomial();
		 binomialDistribution.nextDouble();
		**/
		
		this.papers.add(paper); // Add paper to author's written papers.
		paper.getAuthors().add(this); // Add author to paper's co-author list.
		paper.getSharedDisciplines().addAll(disciplines); // Diffusion of knowledge, pass collective knowledge through paper.
		
		// Get environmental containers.
		Context<ISDoS> context = ContextUtils.getContext(this);
		// Get co-authership network.
		Network<ISDoS> net = (Network<ISDoS>)context.getProjection("coAuthorship network");
		
		if(RandomHelper.nextDoubleFromTo(0, 1) < pW){
			System.out.println("Next walk"); // A tracker for debugging.
			
			double[] pdf = new double[net.getDegree(this)]; // Probability distribution function among neighbors of author in network.
			
			double totalWeight = 0; // Total weight of edges in network.
			for(RepastEdge<ISDoS> edge : net.getEdges()){
				totalWeight += edge.getWeight();
			}
			
			int index = 0;
			ArrayList<Object> neighbours = new ArrayList<Object>();
			for(RepastEdge<ISDoS> edge : net.getEdges(this)){
				double weightOfedge = edge.getWeight();
				double transitionProbability = weightOfedge / totalWeight; // Transition probability as explained above.
				pdf[index] = transitionProbability;
				index++;
				neighbours.add(edge.getTarget());
			}
			
			RandomHelper.createEmpiricalWalker(pdf, 0); // A random distribution according to given pdf.
			int indexOfneighbour = RandomHelper.getEmpiricalWalker().nextInt();
			Author nextAuthor = (Author)neighbours.get(indexOfneighbour); // Get next possible co-author candidate.
			nextAuthor.step(pW, paper); // Continue to walk through next candidate author.
			
		} else {
			// Since walk finished,
			paper.decideDiscipline(context); // Decide main discipline of paper.
			paper.updateDisciplinesOfAuthors(); // Update disciplines of co-authors.
			paper.updateCoAuthorNetwork(); // Update network according new knowledge.
		}
	}
}

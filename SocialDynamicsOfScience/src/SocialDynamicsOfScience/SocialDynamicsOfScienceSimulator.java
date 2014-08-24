package SocialDynamicsOfScience;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

/**
 * Context Simulator for Simulation.
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
public class SocialDynamicsOfScienceSimulator implements ISDoS {
		
	/**
	 * Controls the number of authors per paper. Probability of random walk to continue as it is explained above.
	 * 
	 * A step in a biased random walk.
	 * We model these behaviors through a biased random walk. 
	 * The length of the random walk determines the number of co-authors. 
	 * At each step in the walk, the author visits a node (starting with itself) and decides to stop with probability pW , or to search for additional authors with probability 1 − pW .
	 * 
	 */
	private Double pW;
	/**
	 * Controls the number of papers per author.
	 * 
	 * At every time step, with probability pN, we add a new author to the network with the new paper. 
	 * The parameter pN regulates the ratio of papers to authors.
	 * 
	 * 
	 */
	private Double pN;
	/**
	 * Represents the frequency of network split and merge events, and controls the number of disciplines.
	 * 
	 * A novel mechanism to model the evolution of disciplines by splitting and merging communities in the social collaboration network.
	 * The idea, motivated by the earlier observations from the APS data, is that the birth or decline of a discipline should correspond to an increase in the modularity of the network.
	 * Two such events may occur at each time step with probability pD.
	 * 
	 * 
	 */
	private Double pD;
	
	/**
	 * Constructor for class.
	 * 
	 * Parameters will obtained from data sets to calculate related, portions. 
	 * Simulation uses them as probability distribution seeds.
	 *
	 * @param pW Controls the number of authors per paper. Probability of random walk to continue.
	 * @param pN Controls the number of papers per author.
	 * @param pD Represents the frequency of network split and merge events, and controls the number of disciplines.
	 * @see SocialDynamicsOfScienceBuilder
	 */
	public SocialDynamicsOfScienceSimulator(Double pW, Double pN, Double pD) {
		this.pW = pW;
		this.pN = pN;
		this.pD = pD;
	}
		
	// Getters and setters.
	
	public Double getpW() {
		return pW;
	}

	public void setpW(Double pW) {
		this.pW = pW;
	}

	public Double getpN() {
		return pN;
	}

	public void setpN(Double pN) {
		this.pN = pN;
	}

	public Double getpD() {
		return pD;
	}

	public void setpD(Double pD) {
		this.pD = pD;
	}

	/**
	 * Main scheduled method for simulation.
	 * At every time step, a new paper is added to the network. 
	 * First author is chosen uniformly at random, so every author has a chance to publish a paper.
	 * At every time step, with probability pN , we add a new author to the network with the new paper. 
	 * The parameter pN regulates the ratio of papers to authors.
	 * The new author is the first author of the new paper.
	 * To generate other collaborators, an existing author is first selected uniformly at random as the first co-author.
	 * Then the random walk procedure is followed to pick additional collaborators. 
	 * The new author acquires the main topic of the paper.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@ScheduledMethod(start = 1, interval = 1)
	public void select() {
		Context<ISDoS> context = ContextUtils.getContext(this);
		System.out.println("stepper");
		// Randomly get an author from context.
		Iterable<ISDoS> authors = context.getRandomObjects(Author.class , 1); // Since this methods returns iterable object,
		// Selects an author uniformly distributed randomly from context.
		Author author = new Author();
		// An implementation walk around, cused by usage of built-in context.getRandomObjects(Author.class , 1) method.
		for(ISDoS a : authors){
			author = (Author)a;
		}
		
		// Calls biased random walk by passing relavent data.
		Paper paper = new Paper(context.getObjects(Paper.class).size());
		context.add(paper);
		author.step(pW, paper);
		
		/**
		 * Valid but deprecated method for probabilistic distribution of walk,
		 * TODO: Fix this in later mature versions.	
		RandomHelper.createBinomial(getNumberOfAuthors(), pN);
		Binomial binomialDistribution = RandomHelper.getBinomial();
		binomialDistribution.nextDouble();
		**/
		
		// With a probability seed that given as a parameter adds an author network or not.
		if(RandomHelper.nextDoubleFromTo(0, 1) < this.pN){
			Author newAuthor = new Author(context.getObjects(Author.class).size());
			context.add(newAuthor);
			Paper newPaper =  new Paper(context.getObjects(Paper.class).size());
			context.add(newPaper);
			newPaper.getAuthors().add(newAuthor);
			newAuthor.getPapers().add(newPaper);
			
			Iterable<ISDoS> coAuthors = context.getRandomObjects(Author.class , 1);
			// Selects an author uniformly distributed randomly from context.
			Author coAuthor = new Author();
			for(ISDoS a : coAuthors){
				coAuthor = (Author)a;
			}
			coAuthor.step(pW, newPaper);
		}
		/**
		 * We introduce a novel mechanism to model the evolution of disciplines by splitting and merging communities in the social collaboration network.
		 * The idea, motivated by the earlier observations from the APS data, is that the birth or decline of a discipline should correspond to an increase in the modularity of the network.
		 *  Two such events may occur at each time step with probability pD .
		 */
		if(RandomHelper.nextDoubleFromTo(0, 1) < this.pD){
			evoluateDisciplines();
		}
		
	}

	/**
	 * We introduce a novel mechanism to model the evolution of disciplines by splitting and merging communities in the social collaboration network.
	 * The idea, motivated by the earlier observations from the APS data, is that the birth or decline of a discipline should correspond to an increase in the modularity of the network.
	 *  Two such events may occur at each time step with probability pD .
	 */
	private void evoluateDisciplines() {
		split();
		merge();
		
	}

	/**
	 * * TODO: Implement.
	 * 
	 * For a split event we select a random discipline with its co-author network and decide whether a new discipline should emerge from a subset of this community. 
	 * We partition the co-author network into two clusters.
	 * If the modularity of the partition is higher than that of the single discipline, there are more collaborations within each cluster than across the two.
	 * We then split the smaller community as a new discipline.
	 * In this case the papers whose authors are all in the new community are relabeled to reﬂect the emergent discipline. 
	 * Borderline papers with authors in both old and new disciplines are labeled according to the discipline of the majority of authors.
	 * Some authors may as a result belong to both old and new discipline.
	 * For our context labels or groups are named as communities.
	 */
	private void merge() {
		// TODO 
		
	}

	
	/**
	 * TODO: Implement.
	 * 
	 * For a merge event we randomly select two disciplines with at least one common author.
	 * If the modularity obtained by merging the two groups is higher than that of the partitioned groups, the collaborations across the two communities are stronger than those within each one.
	 * The two are then merged into a single new discipline.
	 * In this case all the papers in the two old disciplines are relabeled to reﬂect the new one.
	 * 
	 */
	private void split() {
		// TODO 
		
	}
}

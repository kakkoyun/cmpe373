package SocialDynamicsOfScience;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;


/**
 * Context Builder for Simulation.
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
public class SocialDynamicsOfScienceBuilder extends DefaultContext<ISDoS>
implements ContextBuilder<ISDoS>  {

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
	 */
	private Double pN;
	/**
	 * Represents the frequency of network split and merge events, and controls the number of disciplines.
	 * 
	 * We introduce a novel mechanism to model the evolution of disciplines by splitting and merging communities in the social collaboration network.
	 * The idea, motivated by the earlier observations from the APS data, is that the birth or decline of a discipline should correspond to an increase in the modularity of the network.
	 * Two such events may occur at each time step with probability pD.
	 * 
	 * 
	 * 
	 */
	private Double pD;

	
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
	 * A method to obtain number of author agents in context.
	 * @return Number of author agents in context.
	 */
	public int getAuthorCount(){
		return getObjects(Author.class).size();
	}
	/**
	 * A method to obtain number of paper agents in context.
	 * @return Number of paper agents in context.
	 */
	public int getPaperCount(){
		return getObjects(Paper.class).size();
	}
	/**
	 * A method to obtain number of discipline agents in context.
	 * @return Number of discipline agents in context.
	 */
	public int getDisciplineCount(){
		return getObjects(Discipline.class).size(); 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Context<ISDoS> build(Context<ISDoS> context) {

		// Set id for context.
		context.setId("SocialDynamicsOfScience");

		// Build network for context.
		NetworkBuilder<ISDoS> netBuilder = new NetworkBuilder<ISDoS>(
				"coAuthorship network",
				context, false);
		netBuilder.buildNetwork();

		// Get rates for simulation as parameters.
		Parameters params = RunEnvironment.getInstance().getParameters();
		setpW((Double) params.getValue("authors_per_paper"));
		setpD((Double) params.getValue("papers_per_author"));
		setpN((Double) params.getValue("papers_per_discipline"));


		Network<ISDoS> net = (Network<ISDoS>)context.getProjection("coAuthorship network");

		// Initialize a network for simulation.
		// TODO: Get an initial data set from outside from a file.
		Author A = new Author(0);
		Discipline D = new Discipline(0);
		Paper P = new Paper(0);

		// Author our only agent but we need to keep track of other entities.
		context.add(A);
		context.add(D);
		context.add(P);

		net.addEdge(A, A);
		A.getDisciplines().add(D);
		A.getPapers().add(P);
		P.getAuthors().add(A);
		P.setDiscipline(D);


		// Since in this stage, I do not implemented split/merge of disciplines,
		// - I need more than one discipline.

		Author A1 = new Author(1);
		Author A2 = new Author(2);
		Author A3 = new Author(3);
		Author A4 = new Author(4);

		Discipline CS = new Discipline(1);
		Discipline MATH = new Discipline(2);
		Discipline PHY = new Discipline(3);
		Discipline PHIL = new Discipline(4);

		Paper P1 = new Paper(1);
		Paper P2 = new Paper(2);
		Paper P3 = new Paper(3);
		Paper P4 = new Paper(4);

		context.add(A1);
		context.add(A2);
		context.add(A3);
		context.add(A4);

		context.add(P1);
		context.add(P2);
		context.add(P3);
		context.add(P4);

		context.add(CS);
		context.add(MATH);
		context.add(PHY);
		context.add(PHIL);

		net.addEdge(A1, A2);
		net.addEdge(A2, A3);
		net.addEdge(A1, A3);
		A1.getDisciplines().add(CS);
		A1.getPapers().add(P1);
		P1.getAuthors().add(A1);
		A2.getDisciplines().add(CS);
		A2.getPapers().add(P1);
		P1.getAuthors().add(A2);
		A3.getDisciplines().add(CS);
		A3.getPapers().add(P1);
		P1.getAuthors().add(A3);
		P1.setDiscipline(CS);

		net.getEdge(A2, A3).setWeight(2);
		A2.getDisciplines().add(MATH);
		A2.getPapers().add(P2);
		P2.getAuthors().add(A2);
		A3.getDisciplines().add(MATH);
		A3.getPapers().add(P2);
		P2.getAuthors().add(A3);
		P2.setDiscipline(MATH);

		net.addEdge(A3, A3);
		A3.getDisciplines().add(PHY);
		A3.getPapers().add(P3);
		P3.getAuthors().add(A3);
		P3.setDiscipline(PHY);

		net.addEdge(A4, A);
		A4.getDisciplines().add(PHIL);
		A4.getPapers().add(P4);
		P4.getAuthors().add(A4);
		A.getDisciplines().add(PHIL);
		A.getPapers().add(P4);
		P4.getAuthors().add(A);
		P4.setDiscipline(PHIL);

		// An example community.
		Community community = new Community();
		context.add(community);

		// Simulator runner an super wrapper class instance to run simulation.
		SocialDynamicsOfScienceSimulator simulator = new SocialDynamicsOfScienceSimulator(pW, pN, pD);
		context.add(simulator);

		System.out.println("Network initialized"); // A tracker for debugging.
		return context;
	}
}

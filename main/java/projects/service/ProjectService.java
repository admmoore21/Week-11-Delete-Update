package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}
	
	// calls the method from the DAO class fetchAllProducts();
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	/* Fetches the method from ProjectDao and if no project exists for that number
	 * throws the NoSuchElementException and prints that the project does not exist.*/
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(
		() -> new NoSuchElementException("Project with project ID=" 
			+ projectId + " does not exist."));
	}

	/*Checks to see whether or not the UPDATE was successful.  If not successful,
	 * the DAO returns false, then the exception will be thrown and the message 
	 * "project with ID = ? does not exist.*/
	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" 
					+ project.getProjectId() + " does not exist.");
		}
	}

	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
	}

}

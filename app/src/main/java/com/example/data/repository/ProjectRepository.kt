package com.example.data.repository

import com.example.data.dao.ProjectDao
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProjectsByType(type: String): Flow<List<ProjectEntity>> =
        projectDao.getProjectsByType(type)

    suspend fun getProjectById(id: Long): ProjectEntity? =
        projectDao.getProjectById(id)

    suspend fun saveProject(project: ProjectEntity): Long =
        projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) =
        projectDao.updateProject(project)

    suspend fun deleteProject(project: ProjectEntity) =
        projectDao.deleteProject(project)

    suspend fun deleteById(id: Long) =
        projectDao.deleteById(id)
}

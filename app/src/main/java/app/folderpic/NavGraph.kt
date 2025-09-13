package app.folderpic

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import app.folderpic.ui.FolderGrid
import app.folderpic.ui.FolderContent
import app.folderpic.ui.MediaView


@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        startDestination = "folderGrid",
        navController = navController,
        enterTransition = { EnterTransition.None },
		exitTransition = { ExitTransition.None }
    ) {
        composable(route = "folderGrid") {
            FolderGrid(
                onFolderClick = { folderId, folderName ->
                    navController.navigate("folderContent/$folderId/$folderName")
                }
            )
        }
        composable(route = "folderContent/{folderId}/{folderName}") {
            val folderId = it.arguments?.getString("folderId")?.toLong() ?: 0L
            val folderName = it.arguments!!.getString("folderName")!!
            
            FolderContent(
                folderId = folderId,
                folderName = folderName,
                onMediaClick = { mediaId ->
                    navController.navigate("mediaView/$mediaId/$folderId")
                }
            )
        }
        composable("mediaView/{mediaId}/{folderId}") {
            val mediaId = it.arguments?.getString("mediaId")?.toLong() ?: 0L
            val folderId = it.arguments?.getString("folderId")?.toLong() ?: 0L
            MediaView(
                mediaId = mediaId,
                folderId = folderId
            )
        }
    }
}
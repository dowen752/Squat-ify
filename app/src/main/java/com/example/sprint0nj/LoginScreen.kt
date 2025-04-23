package com.example.sprint0nj

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import com.example.sprint0nj.data.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.example.sprint0nj.RegisterDialog as RegisterDialog1 // android studio did this because it wasn't importing initially

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showRegisterDialog by remember { mutableStateOf(false) }
    val firestoreRepository = remember { FirestoreRepository()}
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val imageUrl = "https://i.imgur.com/XEIK40Z.png"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Squat-ify",
                fontSize = 45.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Login Screen Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.width(260.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color(0xFF212121),
                    unfocusedContainerColor = Color(0xFF212121)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.width(260.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color(0xFF212121),
                    unfocusedContainerColor = Color(0xFF212121)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(password == ""){
                        password += " "
                    }
                    val stupidDumbFakeEmail = "${username}@squatify.com"

                    auth.signInWithEmailAndPassword(stupidDumbFakeEmail, password)
                        .addOnCompleteListener{ authentication ->
                            if(authentication.isSuccessful){

                                navController.navigate("library")
                            }
                            else{
                                Toast.makeText(context, "Incorrect Username or Password.", Toast.LENGTH_LONG).show()
                            }
                        }



                },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
            ) {
                Text("Log In", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showRegisterDialog = true },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF212121))
            ) {
                Text("Register", color = Color.White)
            }

        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF388E3C))
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .clickable {

                    navController.navigate("library")
                }
        )
    }
    if (showRegisterDialog) {
        RegisterDialog1(
            onDismiss = {
                showRegisterDialog = false
            },
            onConfirm = { newUsername, newPassword ->
                // Firebase createUser logic
                firestoreRepository.postUser(
                    username = newUsername,
                    password = newPassword,
                    displayName = "$newUsername",
                    onSuccess = {
                        Toast.makeText(context, "Account Created Successfully.", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "There was an error creating your account.", Toast.LENGTH_LONG).show()
                    }

                )
                showRegisterDialog = false

            }
        )
    }
    }

@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}

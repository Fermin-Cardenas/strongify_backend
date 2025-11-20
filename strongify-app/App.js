import React, { useState, useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Stacks
import AuthStack from './navigation/AuthStack';
import ClientTabs from './navigation/ClientTabs';
import CoachTabs from './navigation/CoachTabs';

// Services
import { getStoredToken, getStoredUserRole } from './services/storageService';

const Stack = createStackNavigator();

export default function App() {
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const token = await getStoredToken();
      const role = await getStoredUserRole();
      
      if (token) {
        setIsAuthenticated(true);
        setUserRole(role);
      }
    } catch (error) {
      console.error('Error checking auth status:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    // Puedes agregar un componente de loading aquí
    return null;
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {!isAuthenticated ? (
          <Stack.Screen name="Auth" component={AuthStack} />
        ) : userRole === 'COACH' || userRole === 'ENTRENADOR' ? (
          <Stack.Screen name="Coach" component={CoachTabs} />
        ) : (
          <Stack.Screen name="Client" component={ClientTabs} />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}


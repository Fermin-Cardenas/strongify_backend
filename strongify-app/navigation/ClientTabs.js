import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';

// Screens
import ScheduleScreen from '../screens/client/ScheduleScreen';
import BookingScreen from '../screens/client/BookingScreen';
import PaymentScreen from '../screens/client/PaymentScreen';
import ProfileScreen from '../screens/client/ProfileScreen';

const Tab = createBottomTabNavigator();

export default function ClientTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName;

          if (route.name === 'Schedule') {
            iconName = focused ? 'calendar' : 'calendar-outline';
          } else if (route.name === 'Booking') {
            iconName = focused ? 'book' : 'book-outline';
          } else if (route.name === 'Payment') {
            iconName = focused ? 'card' : 'card-outline';
          } else if (route.name === 'Profile') {
            iconName = focused ? 'person' : 'person-outline';
          }

          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#6200ee',
        tabBarInactiveTintColor: 'gray',
        headerShown: true,
      })}
    >
      <Tab.Screen 
        name="Schedule" 
        component={ScheduleScreen}
        options={{ title: 'Mi Agenda' }}
      />
      <Tab.Screen 
        name="Booking" 
        component={BookingScreen}
        options={{ title: 'Reservar' }}
      />
      <Tab.Screen 
        name="Payment" 
        component={PaymentScreen}
        options={{ title: 'Membresía' }}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{ title: 'Perfil' }}
      />
    </Tab.Navigator>
  );
}


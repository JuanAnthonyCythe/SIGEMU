import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { RutaProtegida } from './components/RutaProtegida';
import { Login } from './pages/Login';
import { Inventario } from './pages/Inventario';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Ruta Pública: Login */}
          <Route path="/login" element={<Login />} />

          {/* Ruta Protegida: Inventario */}
          <Route
            path="/inventario"
            element={
              <RutaProtegida>
                <Inventario />
              </RutaProtegida>
            }
          />

          {/* Redirección por defecto */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

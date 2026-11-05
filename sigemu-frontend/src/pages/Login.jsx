import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';
import { Pill, Lock, User, AlertCircle, ShieldCheck, ArrowRight, Loader2 } from 'lucide-react';

export const Login = () => {
  const [usuario, setUsuario] = useState('');
  const [contrasenia, setContrasenia] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/inventario', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await api.post('/auth/login', {
        usuario: usuario.trim(),
        contrasenia: contrasenia.trim(),
      });

      login(response.data);
      navigate('/inventario', { replace: true });
    } catch (err) {
      if (err.response) {
        if (err.response.status === 401) {
          setError(err.response.data?.mensaje || 'Credenciales inválidas. Por favor verifique sus datos.');
        } else {
          setError(err.response.data?.mensaje || `Error del servidor (${err.response.status}).`);
        }
      } else {
        setError('No se pudo conectar con el servidor backend (puerto 8080).');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleFillDemo = (u, c) => {
    setUsuario(u);
    setContrasenia(c);
    setError(null);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-slate-50 to-teal-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Card Header */}
        <div className="bg-white rounded-2xl shadow-xl border border-slate-100 p-8">
          <div className="flex flex-col items-center text-center mb-8">
            <div className="w-16 h-16 bg-emerald-600 rounded-2xl flex items-center justify-center shadow-lg shadow-emerald-200 mb-4 text-white">
              <Pill className="w-9 h-9" />
            </div>
            <h1 className="text-2xl font-bold text-slate-800 tracking-tight">SIGEMU Farmacia</h1>
            <p className="text-sm text-slate-500 mt-1 font-medium">
              Universidad Nacional de San Cristóbal de Huamanga
            </p>
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 mt-3 border border-emerald-200">
              <ShieldCheck className="w-3.5 h-3.5" />
              Acceso Seguro con JWT & Spring Boot 3
            </span>
          </div>

          {error && (
            <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm flex items-start gap-3 animate-fade-in">
              <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5 text-red-500" />
              <div>
                <p className="font-semibold">Error de autenticación</p>
                <p className="text-xs mt-0.5 text-red-600">{error}</p>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">
                Usuario
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <User className="w-5 h-5" />
                </div>
                <input
                  type="text"
                  required
                  value={usuario}
                  onChange={(e) => setUsuario(e.target.value)}
                  placeholder="Ej. admin o farmaceutico"
                  className="w-full pl-11 pr-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 text-sm transition-colors text-slate-800 bg-slate-50/50 focus:bg-white"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-2">
                Contraseña
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-5 h-5" />
                </div>
                <input
                  type="password"
                  required
                  value={contrasenia}
                  onChange={(e) => setContrasenia(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-11 pr-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 text-sm transition-colors text-slate-800 bg-slate-50/50 focus:bg-white"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-xl shadow-md shadow-emerald-200 transition-all duration-150 flex items-center justify-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed cursor-pointer"
            >
              {loading ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />
                  <span>Validando credenciales...</span>
                </>
              ) : (
                <>
                  <span>Ingresar al Sistema</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </form>

          {/* Cuentas de Acceso Rápido */}
          <div className="mt-8 pt-6 border-t border-slate-100">
            <p className="text-xs text-slate-400 font-semibold uppercase tracking-wider mb-3 text-center">
              Credenciales de Demostración
            </p>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <button
                type="button"
                onClick={() => handleFillDemo('admin', 'admin123')}
                className="p-2.5 rounded-lg border border-slate-200 bg-slate-50 hover:bg-emerald-50 hover:border-emerald-300 text-left transition-colors cursor-pointer"
              >
                <div className="font-bold text-slate-800">Admin</div>
                <div className="text-slate-500 text-[11px]">admin / admin123</div>
                <span className="inline-block mt-1 px-1.5 py-0.5 rounded text-[10px] bg-purple-100 text-purple-700 font-semibold">
                  ROL ADMIN
                </span>
              </button>
              <button
                type="button"
                onClick={() => handleFillDemo('farmaceutico', 'farma123')}
                className="p-2.5 rounded-lg border border-slate-200 bg-slate-50 hover:bg-teal-50 hover:border-teal-300 text-left transition-colors cursor-pointer"
              >
                <div className="font-bold text-slate-800">Farmacéutico</div>
                <div className="text-slate-500 text-[11px]">farmaceutico / farma123</div>
                <span className="inline-block mt-1 px-1.5 py-0.5 rounded text-[10px] bg-teal-100 text-teal-700 font-semibold">
                  FARMACEUTICO
                </span>
              </button>
            </div>
          </div>
        </div>

        {/* Footer info */}
        <p className="text-center text-xs text-slate-400 mt-6">
          SIGEMU &copy; {new Date().getFullYear()} - Sistema de Gestión de Medicamentos
        </p>
      </div>
    </div>
  );
};

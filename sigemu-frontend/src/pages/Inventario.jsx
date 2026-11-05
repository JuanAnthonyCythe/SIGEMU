import React, { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axiosConfig';
import { CrearLoteModal } from '../components/CrearLoteModal';
import { RegistrarMovimientoModal } from '../components/RegistrarMovimientoModal';
import { KardexModal } from '../components/KardexModal';
import {
  Pill,
  Search,
  Plus,
  ArrowLeftRight,
  ClipboardList,
  LogOut,
  RefreshCw,
  Shield,
  UserCheck,
  AlertTriangle,
  CheckCircle2,
  XCircle,
  Package,
  Layers,
} from 'lucide-react';

export const Inventario = () => {
  const { user, logout } = useAuth();
  const [lotes, setLotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busqueda, setBusqueda] = useState('');

  // Estados de Modales
  const [modalCrearAbierto, setModalCrearAbierto] = useState(false);
  const [modalMovimientoAbierto, setModalMovimientoAbierto] = useState(false);
  const [modalKardexAbierto, setModalKardexAbierto] = useState(false);
  const [loteSeleccionado, setLoteSeleccionado] = useState(null);

  const esAdmin = user?.rol === 'ADMIN';

  const cargarLotes = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get('/lotes');
      setLotes(response.data || []);
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al cargar los lotes del inventario.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarLotes();
  }, []);

  // Filtrado por número de lote o nombre de medicamento
  const lotesFiltrados = useMemo(() => {
    const query = busqueda.toLowerCase().trim();
    if (!query) return lotes;
    return lotes.filter(
      (l) =>
        l.numeroLote?.toLowerCase().includes(query) ||
        l.nombreMedicamento?.toLowerCase().includes(query) ||
        l.medicamento?.toLowerCase().includes(query)
    );
  }, [lotes, busqueda]);

  // Contadores para métricas
  const metricas = useMemo(() => {
    const total = lotes.length;
    const vigentes = lotes.filter((l) => l.estado === 'VIGENTE').length;
    const stockBajo = lotes.filter((l) => l.estado === 'STOCK_BAJO').length;
    const vencidos = lotes.filter((l) => l.estado === 'VENCIDO' || l.estado === 'AGOTADO').length;
    return { total, vigentes, stockBajo, vencidos };
  }, [lotes]);

  const abrirMovimientoParaLote = (lote) => {
    setLoteSeleccionado(lote);
    setModalMovimientoAbierto(true);
  };

  const abrirKardexParaLote = (lote) => {
    setLoteSeleccionado(lote);
    setModalKardexAbierto(true);
  };

  // Semáforo de Estado
  const renderBadgeEstado = (estado) => {
    switch (estado) {
      case 'VIGENTE':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
            Vigente
          </span>
        );
      case 'STOCK_BAJO':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
            <span className="w-1.5 h-1.5 rounded-full bg-amber-500"></span>
            Stock Bajo
          </span>
        );
      case 'VENCIDO':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-200">
            <span className="w-1.5 h-1.5 rounded-full bg-red-500"></span>
            Vencido
          </span>
        );
      case 'AGOTADO':
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-700 border border-slate-300">
            <span className="w-1.5 h-1.5 rounded-full bg-slate-400"></span>
            Agotado
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs bg-slate-100 text-slate-700">
            {estado}
          </span>
        );
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col">
      {/* Top Navigation Bar */}
      <header className="sticky top-0 z-40 bg-white border-b border-slate-200 shadow-xs">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          {/* Logo & Brand */}
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-emerald-600 rounded-xl flex items-center justify-center text-white shadow-md shadow-emerald-200">
              <Pill className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-lg text-slate-800 tracking-tight">SIGEMU</span>
                <span className="text-xs px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-800 font-semibold">
                  Farmacia UNSCH
                </span>
              </div>
              <p className="text-[11px] text-slate-400 font-medium">Control de Lotes y Kardex</p>
            </div>
          </div>

          {/* User Profile & Logout */}
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-3 pl-4 border-l border-slate-200">
              <div className="text-right hidden sm:block">
                <p className="text-xs font-bold text-slate-800 flex items-center justify-end gap-1">
                  <UserCheck className="w-3.5 h-3.5 text-emerald-600" />
                  {user?.usuario}
                </p>
                <div className="flex items-center justify-end gap-1 mt-0.5">
                  <span
                    className={`inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full ${
                      esAdmin
                        ? 'bg-purple-100 text-purple-700 border border-purple-200'
                        : 'bg-teal-100 text-teal-700 border border-teal-200'
                    }`}
                  >
                    <Shield className="w-3 h-3" />
                    {user?.rol}
                  </span>
                </div>
              </div>

              <button
                onClick={logout}
                title="Cerrar Sesión"
                className="p-2 rounded-xl text-slate-500 hover:text-red-600 hover:bg-red-50 border border-slate-200 transition-colors cursor-pointer"
              >
                <LogOut className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Metric Cards Banner */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-500 uppercase">Total Lotes</p>
              <p className="text-2xl font-bold text-slate-800 mt-1">{metricas.total}</p>
            </div>
            <div className="w-10 h-10 rounded-xl bg-slate-100 text-slate-600 flex items-center justify-center">
              <Layers className="w-5 h-5" />
            </div>
          </div>

          <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-emerald-600 uppercase">Vigentes</p>
              <p className="text-2xl font-bold text-emerald-700 mt-1">{metricas.vigentes}</p>
            </div>
            <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
              <CheckCircle2 className="w-5 h-5" />
            </div>
          </div>

          <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-amber-600 uppercase">Stock Bajo</p>
              <p className="text-2xl font-bold text-amber-700 mt-1">{metricas.stockBajo}</p>
            </div>
            <div className="w-10 h-10 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center">
              <AlertTriangle className="w-5 h-5" />
            </div>
          </div>

          <div className="bg-white p-4 rounded-2xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-red-600 uppercase">Vencidos / Agotados</p>
              <p className="text-2xl font-bold text-red-700 mt-1">{metricas.vencidos}</p>
            </div>
            <div className="w-10 h-10 rounded-xl bg-red-100 text-red-700 flex items-center justify-center">
              <XCircle className="w-5 h-5" />
            </div>
          </div>
        </div>

        {/* Action Bar & Search */}
        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-xs flex flex-col md:flex-row items-center justify-between gap-4">
          {/* Search Box */}
          <div className="relative w-full md:w-96">
            <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
              <Search className="w-4 h-4" />
            </div>
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              placeholder="Buscar por lote o medicamento..."
              className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-slate-50/50 focus:bg-white text-slate-800 transition-colors"
            />
            {busqueda && (
              <button
                onClick={() => setBusqueda('')}
                className="absolute inset-y-0 right-0 pr-3 flex items-center text-xs text-slate-400 hover:text-slate-600 cursor-pointer"
              >
                Limpiar
              </button>
            )}
          </div>

          {/* Action Buttons */}
          <div className="flex items-center gap-3 w-full md:w-auto justify-end">
            <button
              onClick={cargarLotes}
              disabled={loading}
              title="Refrescar lista"
              className="p-2.5 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 hover:text-slate-900 transition-colors cursor-pointer disabled:opacity-50"
            >
              <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
            </button>

            <button
              onClick={() => {
                setLoteSeleccionado(null);
                setModalMovimientoAbierto(true);
              }}
              className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-900 text-white text-sm font-medium shadow-xs transition-colors flex items-center gap-2 cursor-pointer"
            >
              <ArrowLeftRight className="w-4 h-4 text-emerald-400" />
              <span>Movimiento Stock</span>
            </button>

            {/* Botón Nuevo Lote - Restringido visualmente a ADMIN */}
            {esAdmin ? (
              <button
                onClick={() => setModalCrearAbierto(true)}
                className="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium shadow-md shadow-emerald-200 transition-colors flex items-center gap-2 cursor-pointer"
              >
                <Plus className="w-4 h-4" />
                <span>Nuevo Lote</span>
              </button>
            ) : (
              <button
                disabled
                title="Solo usuarios con rol ADMIN pueden registrar nuevos lotes"
                className="px-4 py-2 rounded-xl bg-slate-100 border border-slate-200 text-slate-400 text-sm font-medium flex items-center gap-2 cursor-not-allowed"
              >
                <Plus className="w-4 h-4" />
                <span>Nuevo Lote (Solo Admin)</span>
              </button>
            )}
          </div>
        </div>

        {/* Table of Lots */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50/80 text-slate-500 font-semibold border-b border-slate-200 text-xs uppercase tracking-wider">
                <tr>
                  <th className="py-3.5 px-4">Lote #</th>
                  <th className="py-3.5 px-4">Medicamento</th>
                  <th className="py-3.5 px-4 text-center">Stock Actual / Inicial</th>
                  <th className="py-3.5 px-4">Caducidad</th>
                  <th className="py-3.5 px-4">Estado</th>
                  <th className="py-3.5 px-4 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {loading ? (
                  <tr>
                    <td colSpan="6" className="py-12 text-center text-slate-400">
                      <RefreshCw className="w-6 h-6 animate-spin mx-auto text-emerald-600 mb-2" />
                      <span className="text-xs">Cargando inventario de farmacia...</span>
                    </td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td colSpan="6" className="py-8 text-center text-red-500 text-xs">
                      {error}
                    </td>
                  </tr>
                ) : lotesFiltrados.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="py-12 text-center text-slate-400">
                      <Package className="w-10 h-10 mx-auto text-slate-300 mb-2" />
                      <p className="text-sm font-semibold text-slate-600">No se encontraron lotes</p>
                      <p className="text-xs text-slate-400 mt-0.5">
                        {busqueda
                          ? `No hay resultados coincidentes con "${busqueda}".`
                          : 'El inventario está vacío. Registre un nuevo lote para comenzar.'}
                      </p>
                    </td>
                  </tr>
                ) : (
                  lotesFiltrados.map((lote) => {
                    const nombreMed = lote.nombreMedicamento || lote.medicamento;
                    const stockAct = lote.stockActual ?? lote.stock ?? 0;
                    const stockIni = lote.stockInicial ?? lote.stock ?? stockAct;
                    const porcentajeStock =
                      stockIni > 0
                        ? Math.min(100, Math.round((stockAct / stockIni) * 100))
                        : 0;

                    return (
                      <tr key={lote.numeroLote} className="hover:bg-slate-50/60 transition-colors">
                        {/* Lote */}
                        <td className="py-4 px-4 font-mono font-bold text-slate-800 text-xs">
                          {lote.numeroLote}
                        </td>

                        {/* Medicamento */}
                        <td className="py-4 px-4">
                          <div className="font-semibold text-slate-800">{nombreMed}</div>
                          <span className="text-[11px] text-slate-400">Forma Farmacéutica Estándar</span>
                        </td>

                        {/* Stock */}
                        <td className="py-4 px-4">
                          <div className="max-w-[150px] mx-auto text-center">
                            <span className="font-bold text-slate-800 font-mono">
                              {stockAct}
                            </span>
                            <span className="text-slate-400 text-xs font-mono"> / {stockIni} u.</span>
                            {/* Barra de progreso */}
                            <div className="w-full bg-slate-100 rounded-full h-1.5 mt-1.5 overflow-hidden">
                              <div
                                className={`h-1.5 rounded-full transition-all duration-300 ${
                                  porcentajeStock > 30
                                    ? 'bg-emerald-500'
                                    : porcentajeStock > 0
                                    ? 'bg-amber-500'
                                    : 'bg-red-500'
                                }`}
                                style={{ width: `${porcentajeStock}%` }}
                              />
                            </div>
                          </div>
                        </td>

                        {/* Caducidad */}
                        <td className="py-4 px-4 text-xs font-medium text-slate-600 whitespace-nowrap">
                          {lote.fechaCaducidad}
                        </td>

                        {/* Estado */}
                        <td className="py-4 px-4 whitespace-nowrap">
                          {renderBadgeEstado(lote.estado)}
                        </td>

                        {/* Acciones */}
                        <td className="py-4 px-4 text-right whitespace-nowrap">
                          <div className="inline-flex items-center gap-1.5">
                            {/* Registrar Movimiento */}
                            <button
                              onClick={() => abrirMovimientoParaLote(lote)}
                              title="Registrar Movimiento"
                              className="px-2.5 py-1.5 rounded-lg border border-slate-200 text-slate-700 hover:bg-emerald-50 hover:border-emerald-300 hover:text-emerald-800 text-xs font-medium transition-colors flex items-center gap-1 cursor-pointer"
                            >
                              <ArrowLeftRight className="w-3.5 h-3.5 text-emerald-600" />
                              <span>Movimiento</span>
                            </button>

                            {/* Ver Kardex */}
                            <button
                              onClick={() => abrirKardexParaLote(lote)}
                              title="Auditoría Kardex"
                              className="px-2.5 py-1.5 rounded-lg border border-slate-200 text-slate-700 hover:bg-teal-50 hover:border-teal-300 hover:text-teal-800 text-xs font-medium transition-colors flex items-center gap-1 cursor-pointer"
                            >
                              <ClipboardList className="w-3.5 h-3.5 text-teal-600" />
                              <span>Kardex</span>
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      </main>

      {/* Modales */}
      <CrearLoteModal
        isOpen={modalCrearAbierto}
        onClose={() => setModalCrearAbierto(false)}
        onLoteCreado={cargarLotes}
      />

      <RegistrarMovimientoModal
        isOpen={modalMovimientoAbierto}
        onClose={() => {
          setModalMovimientoAbierto(false);
          setLoteSeleccionado(null);
        }}
        loteSeleccionado={loteSeleccionado}
        lotes={lotes}
        onMovimientoRegistrado={cargarLotes}
      />

      <KardexModal
        isOpen={modalKardexAbierto}
        onClose={() => {
          setModalKardexAbierto(false);
          setLoteSeleccionado(null);
        }}
        lote={loteSeleccionado}
      />
    </div>
  );
};

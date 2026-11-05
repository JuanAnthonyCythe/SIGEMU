import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { X, ClipboardList, ArrowDownRight, ArrowUpRight, Loader2, Calendar, FileText } from 'lucide-react';

export const KardexModal = ({ isOpen, onClose, lote }) => {
  const [movimientos, setMovimientos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && lote?.numeroLote) {
      cargarKardex(lote.numeroLote);
    } else {
      setMovimientos([]);
      setError(null);
    }
  }, [isOpen, lote]);

  const cargarKardex = async (numeroLote) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get(`/inventario/movimientos/lote/${numeroLote}`);
      setMovimientos(response.data || []);
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al obtener los movimientos del Kardex.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !lote) return null;

  const formatearFechaHora = (fechaHoraStr) => {
    if (!fechaHoraStr) return '-';
    try {
      const fecha = new Date(fechaHoraStr);
      return fecha.toLocaleString('es-PE', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      });
    } catch {
      return fechaHoraStr;
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl border border-slate-100 w-full max-w-3xl overflow-hidden flex flex-col max-h-[85vh] animate-scale-in">
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-slate-50/70">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-teal-100 text-teal-700 flex items-center justify-center shadow-sm">
              <ClipboardList className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold text-slate-800">Kardex de Auditoría</h2>
                <span className="font-mono text-xs px-2 py-0.5 rounded-md bg-slate-200 text-slate-700 font-semibold">
                  {lote.numeroLote}
                </span>
              </div>
              <p className="text-xs text-slate-500 font-medium">{lote.nombreMedicamento || lote.medicamento}</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Content */}
        <div className="p-6 overflow-y-auto flex-1">
          {/* Info Summary Banner */}
          <div className="grid grid-cols-3 gap-3 mb-5 p-3 rounded-xl bg-slate-50 border border-slate-100 text-center text-xs">
            <div>
              <div className="text-slate-400 font-medium">Stock Inicial</div>
              <div className="text-base font-bold text-slate-700 mt-0.5">
                {lote.stockInicial ?? lote.stock ?? '-'} u.
              </div>
            </div>
            <div>
              <div className="text-slate-400 font-medium">Stock Actual</div>
              <div className="text-base font-bold text-emerald-600 mt-0.5">
                {lote.stockActual ?? lote.stock ?? '-'} u.
              </div>
            </div>
            <div>
              <div className="text-slate-400 font-medium">Estado</div>
              <div className="text-xs font-bold text-slate-700 mt-1 uppercase tracking-wide">
                {lote.estado}
              </div>
            </div>
          </div>

          {loading ? (
            <div className="py-12 flex flex-col items-center justify-center text-slate-400 gap-2">
              <Loader2 className="w-8 h-8 animate-spin text-teal-600" />
              <span className="text-xs font-medium">Consultando transacciones en Kardex...</span>
            </div>
          ) : error ? (
            <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs text-center">
              {error}
            </div>
          ) : movimientos.length === 0 ? (
            <div className="py-12 text-center text-slate-400">
              <FileText className="w-10 h-10 mx-auto text-slate-300 mb-2" />
              <p className="text-sm font-medium text-slate-600">Sin movimientos registrados</p>
              <p className="text-xs text-slate-400 mt-0.5">
                Aún no se han ejecutado transacciones de INGRESO o SALIDA sobre este lote.
              </p>
            </div>
          ) : (
            <div className="overflow-x-auto border border-slate-100 rounded-xl shadow-xs">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50/80 text-slate-500 font-semibold border-b border-slate-100">
                  <tr>
                    <th className="py-3 px-3.5">Fecha y Hora</th>
                    <th className="py-3 px-3.5">Tipo</th>
                    <th className="py-3 px-3.5 text-right">Cantidad</th>
                    <th className="py-3 px-3.5 text-right">Stock Resultante</th>
                    <th className="py-3 px-3.5">Motivo / Justificación</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {movimientos.map((m) => {
                    const esIngreso = m.tipoMovimiento === 'INGRESO';
                    return (
                      <tr key={m.id} className="hover:bg-slate-50/50 transition-colors">
                        <td className="py-3 px-3.5 text-slate-600 whitespace-nowrap font-medium flex items-center gap-1.5">
                          <Calendar className="w-3.5 h-3.5 text-slate-400" />
                          <span>{formatearFechaHora(m.fechaHora)}</span>
                        </td>
                        <td className="py-3 px-3.5 whitespace-nowrap">
                          <span
                            className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full font-bold text-[11px] ${
                              esIngreso
                                ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                                : 'bg-amber-50 text-amber-700 border border-amber-200'
                            }`}
                          >
                            {esIngreso ? <ArrowDownRight className="w-3 h-3" /> : <ArrowUpRight className="w-3 h-3" />}
                            {m.tipoMovimiento}
                          </span>
                        </td>
                        <td className="py-3 px-3.5 text-right font-mono font-semibold whitespace-nowrap">
                          <span className={esIngreso ? 'text-emerald-600' : 'text-amber-600'}>
                            {esIngreso ? `+${m.cantidad}` : `-${m.cantidad}`}
                          </span>
                        </td>
                        <td className="py-3 px-3.5 text-right font-mono font-bold text-slate-800 whitespace-nowrap">
                          {m.stockResultante} u.
                        </td>
                        <td className="py-3 px-3.5 text-slate-600 max-w-xs truncate" title={m.motivo}>
                          {m.motivo || '-'}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Modal Footer */}
        <div className="px-6 py-3.5 bg-slate-50 border-t border-slate-100 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-900 text-white text-xs font-medium transition-colors cursor-pointer"
          >
            Cerrar Kardex
          </button>
        </div>
      </div>
    </div>
  );
};

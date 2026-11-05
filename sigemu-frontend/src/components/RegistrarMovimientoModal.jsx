import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { X, ArrowDownRight, ArrowUpRight, AlertCircle, Loader2 } from 'lucide-react';

export const RegistrarMovimientoModal = ({ isOpen, onClose, loteSeleccionado, lotes, onMovimientoRegistrado }) => {
  const [numeroLote, setNumeroLote] = useState('');
  const [tipo, setTipo] = useState('SALIDA');
  const [cantidad, setCantidad] = useState('');
  const [motivo, setMotivo] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (loteSeleccionado) {
      setNumeroLote(loteSeleccionado.numeroLote);
    } else if (lotes && lotes.length > 0 && !numeroLote) {
      setNumeroLote(lotes[0].numeroLote);
    }
    setError(null);
  }, [loteSeleccionado, lotes, isOpen]);

  if (!isOpen) return null;

  const currentLote = lotes?.find((l) => l.numeroLote === numeroLote);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const cant = parseInt(cantidad, 10);
    if (isNaN(cant) || cant <= 0) {
      setError('La cantidad debe ser un entero positivo mayor que cero.');
      return;
    }

    const stockDisponible = currentLote ? (currentLote.stockActual ?? currentLote.stock ?? 0) : 0;
    if (tipo === 'SALIDA' && currentLote && cant > stockDisponible) {
      setError(`Stock insuficiente. El lote solo dispone de ${stockDisponible} unidades.`);
      return;
    }

    setLoading(true);

    try {
      await api.post('/inventario/movimientos', {
        numeroLote,
        tipo,
        cantidad: cant,
        motivo: motivo.trim() || (tipo === 'INGRESO' ? 'Reabastecimiento regular' : 'Dispensación a paciente'),
      });

      setCantidad('');
      setMotivo('');
      onMovimientoRegistrado();
      onClose();
    } catch (err) {
      if (err.response) {
        setError(err.response.data?.mensaje || err.response.data?.error || 'Error al procesar el movimiento.');
      } else {
        setError('Error al conectar con el servidor.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
      <div className="bg-white rounded-2xl shadow-2xl border border-slate-100 w-full max-w-lg overflow-hidden animate-scale-in">
        {/* Modal Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 bg-slate-50/70">
          <div className="flex items-center gap-2">
            <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${tipo === 'INGRESO' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'}`}>
              {tipo === 'INGRESO' ? <ArrowDownRight className="w-5 h-5" /> : <ArrowUpRight className="w-5 h-5" />}
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-800">Registrar Movimiento de Stock</h2>
              <p className="text-xs text-slate-500">Transacción auditada en Kardex en tiempo real</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3.5 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs flex items-start gap-2.5">
              <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5 text-red-500" />
              <span>{error}</span>
            </div>
          )}

          {/* Selector de Tipo (INGRESO / SALIDA) */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-2">
              Tipo de Operación <span className="text-red-500">*</span>
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setTipo('SALIDA')}
                className={`py-2.5 px-4 rounded-xl border text-sm font-semibold flex items-center justify-center gap-2 transition-all cursor-pointer ${
                  tipo === 'SALIDA'
                    ? 'border-amber-500 bg-amber-50/80 text-amber-800 shadow-sm'
                    : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'
                }`}
              >
                <ArrowUpRight className="w-4 h-4 text-amber-600" />
                <span>SALIDA (Dispensar)</span>
              </button>
              <button
                type="button"
                onClick={() => setTipo('INGRESO')}
                className={`py-2.5 px-4 rounded-xl border text-sm font-semibold flex items-center justify-center gap-2 transition-all cursor-pointer ${
                  tipo === 'INGRESO'
                    ? 'border-emerald-500 bg-emerald-50/80 text-emerald-800 shadow-sm'
                    : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'
                }`}
              >
                <ArrowDownRight className="w-4 h-4 text-emerald-600" />
                <span>INGRESO (Reabastecer)</span>
              </button>
            </div>
          </div>

          {/* Lote Seleccionado */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Lote de Medicamento <span className="text-red-500">*</span>
            </label>
            <select
              value={numeroLote}
              onChange={(e) => setNumeroLote(e.target.value)}
              required
              className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
            >
              {lotes?.map((l) => {
                const nombreMed = l.nombreMedicamento || l.medicamento;
                const stockAct = l.stockActual ?? l.stock ?? 0;
                return (
                  <option key={l.numeroLote} value={l.numeroLote}>
                    {l.numeroLote} - {nombreMed} (Stock actual: {stockAct})
                  </option>
                );
              })}
            </select>
            {currentLote && (
              <div className="mt-1.5 flex items-center justify-between text-xs text-slate-500 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-100">
                <span>
                  Stock Actual:{' '}
                  <strong className="text-slate-800">
                    {currentLote.stockActual ?? currentLote.stock ?? 0}
                  </strong>{' '}
                  u.
                </span>
                <span>
                  Estado:{' '}
                  <span className="font-semibold text-emerald-700">{currentLote.estado}</span>
                </span>
              </div>
            )}
          </div>

          {/* Cantidad */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Cantidad de Unidades <span className="text-red-500">*</span>
            </label>
            <input
              type="number"
              required
              min="1"
              value={cantidad}
              onChange={(e) => setCantidad(e.target.value)}
              placeholder="Ej. 10"
              className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          {/* Motivo */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Motivo o Justificación (Auditoría Kardex)
            </label>
            <input
              type="text"
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              placeholder={tipo === 'INGRESO' ? 'Ej. Compra directa a laboratorio' : 'Ej. Receta médica #4521'}
              className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          {/* Modal Footer */}
          <div className="pt-4 flex items-center justify-end gap-3 border-t border-slate-100">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors cursor-pointer"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className={`px-5 py-2 rounded-xl text-white text-sm font-medium shadow-md transition-colors flex items-center gap-2 disabled:opacity-60 cursor-pointer ${
                tipo === 'INGRESO'
                  ? 'bg-emerald-600 hover:bg-emerald-700 shadow-emerald-200'
                  : 'bg-amber-600 hover:bg-amber-700 shadow-amber-200'
              }`}
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Procesando...</span>
                </>
              ) : (
                <span>Confirmar {tipo}</span>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

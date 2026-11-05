import React, { useState } from 'react';
import api from '../api/axiosConfig';
import { X, PlusCircle, AlertCircle, Loader2 } from 'lucide-react';

export const CrearLoteModal = ({ isOpen, onClose, onLoteCreado }) => {
  const [numeroLote, setNumeroLote] = useState('');
  const [nombreMedicamento, setNombreMedicamento] = useState('');
  const [stockInicial, setStockInicial] = useState('');
  const [fechaCaducidad, setFechaCaducidad] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    setError(null);

    const stock = parseInt(stockInicial, 10);
    if (isNaN(stock) || stock <= 0) {
      setError('El stock inicial debe ser un número entero mayor que cero.');
      return;
    }

    if (!fechaCaducidad) {
      setError('La fecha de caducidad es obligatoria (formato AAAA-MM-DD).');
      return;
    }

    setLoading(true);

    try {
      const payload = {
        numeroLote: numeroLote.trim(),
        medicamento: nombreMedicamento.trim(),
        stockInicial: stock,
        fechaCaducidad: fechaCaducidad, // Formato YYYY-MM-DD
      };

      await api.post('/lotes', payload);

      // Limpiar formulario y notificar al padre
      setNumeroLote('');
      setNombreMedicamento('');
      setStockInicial('');
      setFechaCaducidad('');
      onLoteCreado();
      onClose();
    } catch (err) {
      if (err.response) {
        if (err.response.status === 403) {
          setError('Acceso denegado (403): Solo usuarios con rol ADMIN pueden registrar lotes.');
        } else {
          setError(err.response.data?.mensaje || err.response.data?.error || 'Error al crear el lote.');
        }
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
            <div className="w-8 h-8 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center">
              <PlusCircle className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-800">Registrar Nuevo Lote</h2>
              <p className="text-xs text-slate-500">Operación restringida a perfil Administrador</p>
            </div>
          </div>
          <button
            type="button"
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

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">
                Número de Lote <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                required
                value={numeroLote}
                onChange={(e) => setNumeroLote(e.target.value.toUpperCase())}
                placeholder="Ej. LOT-2026-001"
                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 font-mono"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1.5">
                Stock Inicial <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                required
                min="1"
                value={stockInicial}
                onChange={(e) => setStockInicial(e.target.value)}
                placeholder="Ej. 100"
                className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Nombre del Medicamento <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              required
              value={nombreMedicamento}
              onChange={(e) => setNombreMedicamento(e.target.value)}
              placeholder="Ej. Paracetamol 500mg Tabletas"
              className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1.5">
              Fecha de Caducidad <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              required
              value={fechaCaducidad}
              onChange={(e) => setFechaCaducidad(e.target.value)}
              className="w-full px-3.5 py-2 rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 text-slate-700"
            />
            <p className="text-[11px] text-slate-400 mt-1">
              La política de alerta clasificará automáticamente el estado según la fecha.
            </p>
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
              className="px-5 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-medium shadow-md shadow-emerald-200 transition-colors flex items-center gap-2 disabled:opacity-60 cursor-pointer"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Guardando...</span>
                </>
              ) : (
                <span>Guardar Lote</span>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

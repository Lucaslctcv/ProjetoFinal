package com.example.demo.Controllers;

import com.example.demo.Models.Convidado;
import com.example.demo.Models.Evento;
import com.example.demo.Repository.ConvidadoRepository;
import com.example.demo.Repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.Option;
import java.util.Optional;

@Controller
public class EventoControllers {

    @Autowired
    private EventoRepository er;

    @Autowired
    private ConvidadoRepository cr;
    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private ConvidadoRepository convidadoRepository;

    @RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
    public String formEvento() {
        return "Evento/formEvento";
    }
    @RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
    public String form(@Validated Evento evento, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos!");
            return "redirect:/cadastrarEvento";
        }

        er.save(evento);
        attributes.addFlashAttribute("mensagem", "Evento cadastrado com sucesso!");

// Redirecionar para a lista de eventos após o cadastro
        return "redirect:/evento"; // Mudei para redirecionar para a lista de eventos
    }

    @RequestMapping("/evento")
    public ModelAndView listarEvento() {
        ModelAndView mv = new ModelAndView("index");
        Iterable<Evento> eventos = er.findAll();
        mv.addObject("evento", eventos);
        return mv;
    }


    @RequestMapping("/deletarEvento")
    public String deletarEvento(long codigo) {
        Evento evento = er.findByCodigo(codigo);
        er.delete(evento);
        return "redirect:/evento";
    }

    // Método GET para detalhes do evento @RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
    public ModelAndView detalhesEventoGet(@PathVariable("codigo") long codigo) {
        ModelAndView mv = new ModelAndView("Evento/detalhesEvento");
        Evento evento = er.findByCodigo(codigo);
        mv.addObject("evento", evento);
        return mv;
    }

    // Método POST para adicionar um convidado ao evento @RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
    public String detalhesEventoPost(@PathVariable("codigo") long codigo, @Validated Convidado convidado, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos!");
            return "redirect:/{codigo}";
        }
        Evento evento = er.findByCodigo(codigo);
        convidado.setEvento(evento);
        cr.save(convidado);
        attributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso!");
        return "redirect:/{codigo}";
    }

    @RequestMapping("/deletarConvidado")
    public String deletarConvidado(String rg) {
        Convidado convidado = cr.findByRg(rg);
        if (convidado == null) {
            return "redirect:/erro"; // Redirecionar para uma página de erro
        }
        cr.delete(convidado);

        Evento evento = convidado.getEvento();
        if (evento == null) {
            return "redirect:/erro"; // Redirecionar se não encontrar evento
        }

        long codigoLong = evento.getCodigo();
        System.out.println("Código do evento: " + codigoLong); // Para depuração

// Redirecionar para a lista de convidados do evento
        return "redirect:/evento/" + codigoLong; // Altere para o caminho correto da lista
    }
    @GetMapping("/evento/{codigo}")
    public String listarConvidados(@PathVariable long codigo, Model model) {
// Usa findById que retorna um Optional
        Optional<Evento> optionalEvento = eventoRepository.findById(String.valueOf(codigo));

        if (!optionalEvento.isPresent()) {
            return "redirect:/erro"; // Redirecionar se não encontrar o evento
        }

        Evento evento = optionalEvento.get(); // Obter o evento do Optional
        model.addAttribute("evento", evento);
        model.addAttribute("convidados", convidadoRepository.findByEvento(evento));

        return "listaConvidados"; // Nome da sua página de lista de convidados
    }

// @RequestMapping(value="/{codigo}", method=RequestMethod.POST)
// public String detalhesEventoPost(@PathVariable("codigo") long codigo, @Validated Convidado convidado, BindingResult result, RedirectAttributes attributes){
// if(result.hasErrors()){
// attributes.addFlashAttribute("mensagem", "Verifique os campos!");
// return "redirect:/{codigo}";
// }
// Evento evento = er.findByCodigo(codigo);
// convidado.setEvento(evento);
// cr.save(convidado);
// attributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso!");
// return "redirect:/{codigo}";
// }



    @RequestMapping(value = "/editarEvento/{codigo}", method = RequestMethod.GET)
    public ModelAndView editarEvento(@PathVariable("codigo") long codigo) {
        ModelAndView mv = new ModelAndView("evento/editarEvento");
        Evento evento = er.findByCodigo(codigo);
        mv.addObject("evento", evento);
        return mv;
    }

    @RequestMapping(value = "/atualizarEvento", method = RequestMethod.POST)
    public String atualizarEvento(@Validated Evento evento, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos!");
            return "redirect:/editarEvento/" + evento.getCodigo();
        }

        er.save(evento); // Salvar as alterações no evento
        attributes.addFlashAttribute("mensagem", "Evento atualizado com sucesso!");
        return "redirect:/evento";
    }



}
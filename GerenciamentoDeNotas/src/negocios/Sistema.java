package negocios;

import dados.*;
import java.util.List;
import java.util.Objects;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import persistencia.*;
import exceptions.*;

public class Sistema {
    private AlunoDAO alunoDAO;
    private SemestreDAO semestreDAO;
    private DisciplinaDAO disciplinaDAO;
    private AvaliacaoDAO avaliacaoDAO;
    // O idAluno = -1 indica que não há aluno logado no momento. Isso será útil para
    // definir qual menu será mostrado quando for implementada a interfae gráfica
    private int idAluno = -1;

    public Sistema() throws ClassNotFoundException, SQLException, SelectException {
        alunoDAO = AlunoDAO.getInstance();
        semestreDAO = SemestreDAO.getInstance();
        disciplinaDAO = DisciplinaDAO.getInstance();
        avaliacaoDAO = AvaliacaoDAO.getInstance();
    }

    public int getIdAluno() {
        return this.idAluno;
    }

    public void deslogar() {
        this.idAluno = -1;
    }

    public String login(String cpfIn, String senhaIn) throws SelectException {
        Aluno aluno = new Aluno();
        aluno = alunoDAO.login(cpfIn, senhaIn);
        if (aluno != null) {
            this.idAluno = aluno.getId();
            return "Logado como " + aluno.getNome();
        } else {
            return "Erro ao fazer login";
        }
    }

    public void cadastraAluno(Aluno aluno) throws InsertException, SelectException {

        alunoDAO.insere(aluno);

    }

    public void removeAluno() throws DeleteException, SelectException {
        Aluno aluno = new Aluno();
        aluno = alunoDAO.select(idAluno);
        alunoDAO.delete(aluno);
    }
    public void cadastraSemestre(Semestre semestre) throws InsertException, SelectException {
        semestreDAO.insere(semestre);
    }
    
    public void removeSemestre(int idSemestre) throws SelectException, DeleteException {
        Semestre semestre = new Semestre();
        semestre = semestreDAO.select(idSemestre);
        semestreDAO.delete(semestre);
    }
    
    public void cadastraDisciplina(Disciplina disciplina) throws InsertException, SelectException {
       disciplinaDAO.insere(disciplina);
    }
    
    public void removeDisciplina(int idDisciplina) throws SelectException, DeleteException {
        Disciplina disciplina = new Disciplina();
        disciplina = disciplinaDAO.select(idDisciplina);
        disciplinaDAO.delete(disciplina);
    }
    public void cadastraAvalicao(Avaliacao avaliacao) throws InsertException, SelectException {
        avaliacaoDAO.insere(avaliacao);
    }
    
    public void removeAvaliacao(int idAvaliacao) throws SelectException, DeleteException {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao = avaliacaoDAO.select(idAvaliacao);
        avaliacaoDAO.delete(avaliacao);
    }
    
    public void adicionaNota(int idAvaliacao, double nota) throws SelectException, UpdateException {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao = avaliacaoDAO.select(idAvaliacao);
        avaliacao.setNota(nota);
        avaliacaoDAO.update(avaliacao);
    }
    /*
    
    public List<Semestre> buscarSemestres() {
        return this.alunos.get(this.idAluno).getSemestres();
    }

    public List<Disciplina> buscarDisciplinas(int idSemestre) {
        return this.alunos.get(this.idAluno).getSemestres().get(idSemestre).getDisciplinas();
    }

    public List<Avaliacao> buscarAvaliacoes(int idSemestre, int idDisciplina) {
        return this.alunos.get(this.idAluno).getSemestres().get(idSemestre).getDisciplinas().get(idDisciplina)
                .getAvaliacoes();
    }

    public double calculaMediaDaDisciplina(int idSemestre, int idDisciplina) {
        List<Avaliacao> avaliacoes = new LinkedList<Avaliacao>();
        avaliacoes = this.alunos.get(this.idAluno).getSemestres().get(idSemestre).getDisciplinas().get(idDisciplina)
                .getAvaliacoes();

        double soma = 0;
        double pesoTotal = 0;
        for (int i = 0; i < avaliacoes.size(); i += 1) {
            // Verifica se a avaliação tem nota ou não e só a inclui no cálculo se tiver
            if (avaliacoes.get(i).getNota() != -1) {
                soma += (avaliacoes.get(i).getNota()) * avaliacoes.get(i).getPeso();
                pesoTotal += avaliacoes.get(i).getPeso();
            }
        }
        return soma / pesoTotal;
    }

    public double calculaMediaDoExame(int idSemestre, int idDisciplina) {
        double mediaFinal = this.calculaMediaDaDisciplina(idSemestre, idDisciplina);
        if (mediaFinal >= 7) {
            return -1;
        }
        if (mediaFinal < 1.7) {
            return -2;
        }
        double exame = -1.5 * mediaFinal + 12.5;
        return exame;

    }

    public String situacaoDoExame(int idSemestre, int idDisciplina) {
        double exame = this.calculaMediaDoExame(idSemestre, idDisciplina);
        if (exame == -2) {
            return "Aluno Reprovado";
        }
        if (exame == -1) {
            return "Aluno Aprovado";
        }
        return "Exame; Média necessária: " + exame;

    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sistema)) {
            return false;
        }
        Sistema sistema = (Sistema) o;
        return Objects.equals(alunos, sistema.alunos);
    }

    // O método alunoPdf() apresenta o resumo de todas as disciplinas de todos os
    // semestres cadastrados, como se fosse um relatório final do aluno.
    public void alunoPdf() {
        Aluno aluno = new Aluno();
        aluno = alunos.get(this.idAluno);
        List<Semestre> semestres = new LinkedList<>();
        semestres = aluno.getSemestres();
        try {
            String dest = "Notas" + aluno.getCpf() + ".pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("Nome do Aluno: " + aluno.getNome()));
            doc.add(new Paragraph("-\tCPF: " + aluno.getCpf()));
            doc.add(new Paragraph("-\tIdade: " + aluno.getIdade()));
            doc.add(new Paragraph("-\tCurso: " + aluno.getCurso()));
            for (int i = 0; i < semestres.size(); i += 1) {
                doc.add(new Paragraph("Semestre: " + semestres.get(i).getNome()));
                for (int i2 = 0; i2 < semestres.get(i).getDisciplinas().size(); i2 += 1) {
                    doc.add(new Paragraph("-\tDisciplina: "
                            + semestres.get(i).getDisciplinas().get(i2).getNome()));
                    doc.add(new Paragraph("-\t\tMédia Final: " + this.calculaMediaDaDisciplina(i, i2)));
                    doc.add(new Paragraph("-\t\tSituação: " + this.situacaoDoExame(i, i2)));
                }

            }
            doc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
*/
}
